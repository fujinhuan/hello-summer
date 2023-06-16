package com.b2c.oms.controller.pipi;

import com.b2c.common.api.ApiResult;
import com.b2c.common.utils.DateUtil;
import com.b2c.entity.result.EnumResultVo;
import com.b2c.entity.result.ResultVo;
import com.b2c.enums.erp.EnumErpOrderStatus;
import com.b2c.service.erp.ErpGoodsService;
import com.b2c.service.erp.ErpSalesOrderService;
import com.b2c.service.oms.DcPiPiOrderService;
import com.b2c.vo.order.OrderImportItem;
import com.b2c.vo.order.OrderImportPiPiEntity;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/pipi")
@RestController
public class PiPiOrderImportAjaxController {
    @Autowired
    private ErpGoodsService erpGoodsService;
    @Autowired
    private DcPiPiOrderService piPiOrderService;

    private static Logger log = LoggerFactory.getLogger(PiPiOrderImportAjaxController.class);

    /***
     * 从菜单打印订单导出excel中批量发货
     * @param file
     * @param req
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    @RequestMapping(value = "/order_excel_import_review_ajax", method = RequestMethod.POST)
    public ApiResult<List<OrderImportPiPiEntity>> orderSendExcel(@RequestParam("excel") MultipartFile file, HttpServletRequest req) throws IOException, InvalidFormatException {

        String fileName = file.getOriginalFilename();
        String dir = System.getProperty("user.dir");
        String destFileName = dir + File.separator + "uploadedfiles_" + fileName;
        System.out.println(destFileName);
        File destFile = new File(destFileName);
        file.transferTo(destFile);
        log.info("/***********导入批批网订单，文件后缀" + destFileName.substring(destFileName.lastIndexOf(".")) + "***********/");
        InputStream fis = null;
        fis = new FileInputStream(destFileName);
        if (fis == null) return new ApiResult<>(502, "没有文件");

        Workbook workbook = null;
        try {
            workbook = new HSSFWorkbook(fis);
        } catch (Exception ex) {
            log.info("/***********导入批批网订单***出现异常：" + ex.getMessage() + "***********/");
            return new ApiResult<>(500, ex.getMessage());
        }

        if (workbook == null) return new ApiResult<>(502, "未读取到csv文件");


        /****************开始处理批批网csv订单****************/
        //订单list
        List<OrderImportPiPiEntity> orderList = new ArrayList<>();
        Sheet sheet = null;

        try {
            sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();//最后一行索引
            Row row = null;

            for (int i = 1; i <= lastRowNum; i++) {
                row = sheet.getRow(i);
                //订单数据
                String orderNum = row.getCell(1).getStringCellValue().replace("\t", "");

                log.info("/***********导入批批网订单***读取到订单ID:" + orderNum + "***********/");

                if (StringUtils.isEmpty(orderNum) == false) {
                    //订单实体
                    OrderImportPiPiEntity order = new OrderImportPiPiEntity();

                    //查找订单是否存在list中
                    boolean bool = orderList.stream().anyMatch(a -> a.getOrderNum().equals(orderNum));
                    if (bool) {
                        //订单存在
                        order = orderList.stream().filter(a -> a.getOrderNum().equals(orderNum)).findFirst().get();

                        //子订单
                        OrderImportItem item = new OrderImportItem();

                        //商品信息
                        String goodsNum = row.getCell(6).getStringCellValue().replace("\t", "");
                        String specName = row.getCell(7).getStringCellValue().replace("\t", "");
//                        String price = row.getCell(8).getStringCellValue().replace("\t", "");
                        String quantity = row.getCell(9).getStringCellValue().replace("\t", "");
                        //商品信息
                        item.setGoodsTitle("");
                        item.setGoodsNumber(goodsNum);
                        item.setSkuInfo(specName);
                        item.setSpecNumber("");

                        //数量
                        item.setQuantity(Long.parseLong(quantity));
                        //单价
//                        item.setPrice(new BigDecimal(price));

                        order.getItems().add(item);
                    } else {
                        order.setOrderNum(orderNum);

                        String dateFormat = "yyyy-MM-dd HH:mm:ss";

                        String orderTime = row.getCell(0).getStringCellValue().replace("\t", "");
                        //订单时间
                        order.setOrderTime(DateUtil.dateToStamp(DateUtil.stringtoDate(orderTime, dateFormat)));
                        order.setOrderTimeStr(orderTime);
                        //收货人
                        String receiver = row.getCell(2).getStringCellValue().replace("\t", "");
                        String mobile = row.getCell(3).getStringCellValue().replace("\t", "");
                        String address = row.getCell(4).getStringCellValue().replace("\t", "");
                        try {
//                            String payMethod = row.getCell(5).getStringCellValue().replace("\t", "");
//                            order.setPayMethod(payMethod);
                        }catch (Exception ee){}
                        //商品信息
                        String goodsNum = row.getCell(6).getStringCellValue().replace("\t", "");
                        String specName = row.getCell(7).getStringCellValue().replace("\t", "");
                        String price = row.getCell(8).getStringCellValue().replace("\t", "");
                        String quantity = row.getCell(9).getStringCellValue().replace("\t", "");

                        //订单状态
                        String statusStr = "等待发货";
                        order.setStatusStr(statusStr);
                        if (statusStr.trim().equals("等待发货")) {
                            order.setStatus(EnumErpOrderStatus.WaitSend.getIndex());
                        }

                        //收货信息
                        order.setContactPerson(receiver);
                        order.setContactMobile(mobile);

                        String[] addr = address.split(" ");
                        order.setAddress(address);
                        if (addr.length > 1) {
                            order.setProvince(addr[0]);
                        }
                        if (addr.length > 2) {
                            order.setCity(addr[1]);
                        }
                        if (addr.length > 3) {
                            order.setArea(addr[2]);
                        }

                        //子订单
                        OrderImportItem item = new OrderImportItem();

                        //商品信息
                        item.setGoodsTitle("");
                        item.setGoodsNumber(goodsNum);
                        item.setSkuInfo(specName);
                        item.setSpecNumber("");

                        //数量
                        item.setQuantity(Long.parseLong(quantity));
                        //单价
                        item.setPrice(new BigDecimal(price));

                        order.getItems().add(item);

                        //添加订单到list
                        orderList.add(order);
                    }


                }


            }


        } catch (Exception ex) {
            log.info("/***********导入批批网订单***出现异常：" + ex.getMessage() + "***********/");
            return new ApiResult<>(500, ex.getMessage());
        }


        int orderCount = orderList.size();
        log.info("/***********导入批批网订单***读取到：" + orderCount + "个订单***********/");

        return new ApiResult<>(0, "SUCCESS", orderList);
    }

    /***
     * 导入批批网订单
     * @param req
     * @return
     */
    @RequestMapping(value = "/order_excel_import_review_submit", method = RequestMethod.POST)
    public ApiResult<String> orderExcelImportSubmit(@RequestBody PiPiOrderImportSubmitReq req) {
//        if (req.getBuyerUserId() == null || req.getBuyerUserId() == 0)
//            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误：没有buyerUserId");

        List<OrderImportPiPiEntity> orderList = req.getOrderList();
        if (orderList == null || orderList.size() == 0)
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误：没有orderList");

        /*************excel订单货号检查，1是否填写了，2是否存在 ********** **************/
        for (var order : orderList) {
            for (var item : order.getItems()) {
                if (StringUtils.isEmpty(item.getGoodsNumber())) {
                    return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误：订单" + order.getOrderNum() + "没有货号");
                } else {
                    //查询商品信息
                    var erpGoods = erpGoodsService.getGoodsEntityByNumber(item.getGoodsNumber());
                    if (erpGoods == null) {
                        return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误：订单" + order.getOrderNum() + "货号:" + item.getGoodsNumber() + "不存在");
                    } else {
                        //重新赋值
                        item.setGoodsId(erpGoods.getId());
                        item.setGoodsTitle(erpGoods.getName());
                        item.setGoodsImg(erpGoods.getImage());
//                        item.setPrice(erpGoods.getWholesalePrice());
                    }
                }
            }
        }

        ResultVo<String> resultVo = piPiOrderService.importExcelOrderForPiPi(orderList, req.getBuyerUserId());

        if (resultVo.getCode() == EnumResultVo.SUCCESS.getIndex()) {
            return new ApiResult<>(0, "SUCCESS", resultVo.getData());
        } else return new ApiResult<>(resultVo.getCode(), resultVo.getMsg());
    }

}
