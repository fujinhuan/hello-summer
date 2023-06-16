package com.b2c.oms.controller.ajax;

import com.b2c.common.api.ApiResult;
import com.b2c.common.utils.DateUtil;
import com.b2c.entity.result.EnumResultVo;
import com.b2c.entity.result.ResultVo;
import com.b2c.enums.erp.EnumErpOrderStatus;
import com.b2c.oms.request.DaiFaOrderImportSubmitReq;
import com.b2c.service.erp.ErpGoodsService;
import com.b2c.service.erp.ErpSalesOrderService;
import com.b2c.vo.order.OrderImportDaiFaEntity;
import com.b2c.vo.order.OrderImportItem;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：
 *
 * @author qlp
 * @date 2019-11-19 09:44
 */
@RequestMapping("/sales")
@RestController
public class SalesOrderImportAjaxDaiFaController {
    @Autowired
    private ErpGoodsService erpGoodsService;
    @Autowired
    private ErpSalesOrderService salesOrderService;

    private static Logger log = LoggerFactory.getLogger(SalesOrderImportAjaxDaiFaController.class);


    /***
     * 一件代发（直播）订单导入excel数据解析ajax
     * @param file
     * @param req
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    @RequestMapping(value = "/daifa_order_import_excel_review", method = RequestMethod.POST)
    public ApiResult<List<OrderImportDaiFaEntity>> orderSendExcel(@RequestParam("excel") MultipartFile file, HttpServletRequest req) throws IOException, InvalidFormatException {

        String fileName = file.getOriginalFilename();
        String dir = System.getProperty("user.dir");
        String destFileName = dir + File.separator + "uploadedfiles_" + fileName;
//        System.out.println(destFileName);
        File destFile = new File(destFileName);
        file.transferTo(destFile);
        log.info("/***********导入一件代发订单开始，文件后缀" + destFileName.substring(destFileName.lastIndexOf(".")) + "***********/");

        XSSFSheet sheet;
        InputStream fis = null;

        fis = new FileInputStream(destFileName);

        //订单list
        List<OrderImportDaiFaEntity> orderList = new ArrayList<>();

        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(fis);
            log.info("/***********导入一件代发订单***读取excel文件成功***********/");

            sheet = workbook.getSheetAt(0);

            int lastRowNum = sheet.getLastRowNum();//最后一行索引
            XSSFRow row = null;

            //订单实体
            OrderImportDaiFaEntity order = new OrderImportDaiFaEntity();
            int currRowNum = 0;
            for (int i = 1; i <= lastRowNum; i++) {
                row = sheet.getRow(i);
                currRowNum = i;
                if (row != null) {

                    //订单号
                    String orderId = row.getCell(0).getStringCellValue();

                    log.info("/***********导入一件代发订单***读取到订单ID:" + orderId + "***********/");
                    if (StringUtils.isEmpty(orderId) == false) {
                        order = new OrderImportDaiFaEntity();

                        order.setId(orderId);

                        String dateFormat = "yyyy/MM/dd HH:mm:ss";
                        try {
                            String createTimeStr = row.getCell(1).getStringCellValue();
                            order.setOrderTime(DateUtil.stringtoDate(createTimeStr, dateFormat));
                            order.setOrderTimeStr(createTimeStr.replace("/", "-"));
                        } catch (Exception e) {
                            return new ApiResult<>(500, "时间格式错误（excel数据应该为文本）：" + e.getMessage());
                        }

                        //订单状态
                        String stateStr = row.getCell(3).getStringCellValue();
                        order.setStatusStr(stateStr);
                        if (stateStr.trim().equals("等待发货")) {
                            order.setStatus(EnumErpOrderStatus.WaitSend.getIndex());
                        }

                        try {
                            order.setSellerMemo(row.getCell(4).getStringCellValue());
                        } catch (Exception e) {
                        }

                        //订单金额
                        try {
                            order.setTotalAmount(new BigDecimal(row.getCell(11).getNumericCellValue()));
                        } catch (Exception e) {
                            order.setTotalAmount(new BigDecimal(0.0));
                        }

                        //收货信息
                        order.setContactPerson(row.getCell(13).getStringCellValue());
                        try {
                            order.setContactMobile(row.getCell(14).getStringCellValue());
                        } catch (Exception e) {
                            return new ApiResult<>(500, "手机号格式错误（excel数据应该为文本）" + e.getMessage());
                        }
                        order.setAddress(row.getCell(15).getStringCellValue());

                        try {
                            String address = row.getCell(15).getStringCellValue();
                            String[] addressArry = address.split(" ");
                            order.setProvince(addressArry[0]);
                            order.setCity(addressArry[1]);
                            order.setArea(addressArry[2]);

                        } catch (Exception e) {
                            log.info("/***********导入一件代发订单***出现不影响结果的异常：收货地址省市区" + e.getMessage() + "***********/");
                        }

                        orderList.add(order);
                    }

                    if (row.getCell(8) != null && row.getCell(8).getCellType() != CellType.BLANK) {
                        //子订单
                        OrderImportItem item = new OrderImportItem();

                        item.setGoodsTitle(row.getCell(6).getStringCellValue());
                        item.setGoodsNumber(row.getCell(7).getStringCellValue());

                        try {
                            item.setSpecNumber(row.getCell(8).getStringCellValue());
                        } catch (Exception e) {
                            return new ApiResult<>(501, "订单：" + orderId + " 商品：" + item.getGoodsTitle() + "【sku为空】");
                        }

                        item.setSkuInfo(row.getCell(9).getStringCellValue());
                        Double quantity = 0.0;
                        try {
                            quantity = row.getCell(10).getNumericCellValue();
                        } catch (Exception e) {
                            quantity = Double.parseDouble(row.getCell(10).getStringCellValue());
                        }

                        item.setQuantity(quantity.longValue());

                        order.getItems().add(item);
                    }
                }
            }


        } catch (Exception ex) {
//            workbook = new HSSFWorkbook(fis);
            log.info("/***********导入一件代发订单***出现异常：" + ex.getMessage() + "***********/");
            return new ApiResult<>(500, ex.getMessage());
        }


//        int orderCount = orderList.size();
//        log.info("/***********导入一件代发订单***读取到：" + orderCount + "个订单，开始插入到数据库***********/");

        return new ApiResult<>(0, "SUCCESS", orderList);

//        ResultVo<Integer> resultVo = service.importExcelDaiFaOrder(orderList);
//        if (resultVo.getCode() == EnumResultVo.SUCCESS.getIndex()) {
//
//            return new ApiResult<>(0, "SUCCESS", "总共：" + orderCount + " 成功：" + resultVo.getData());
//        } else return new ApiResult<>(resultVo.getCode(), resultVo.getMsg());

    }

    /**
     * 一件代发（直播）订单提交
     *
     * @param req
     * @return
     */
    @RequestMapping(value = "/daifa_order_import_submit", method = RequestMethod.POST)
    public ApiResult<String> orderExcelImportSubmit(@RequestBody DaiFaOrderImportSubmitReq req, HttpServletRequest request) {
        if (req.getBuyerUserId() == null || req.getBuyerUserId() == 0)
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误：没有buyerUserId");

        if (req.getOrderList() == null || req.getOrderList().size() == 0)
            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误：没有orderList");

        HttpSession session = request.getSession();
        Object userId = request.getSession().getAttribute("userId");
        if (Integer.parseInt(userId.toString())!=1)  return new ApiResult<>(401, "permission_denied");

        int orderCount = req.getOrderList().size();

        List<OrderImportDaiFaEntity> orderList = req.getOrderList();

        /*************excel订单货号检查，1是否填写了，2是否存在 ********** **************/
        for (var order : orderList) {
            for (var item : order.getItems()) {
                //判断编码不能为空
                if (StringUtils.isEmpty(item.getGoodsNumber())) {
                    return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误：订单" + order.getId() + "没有商品编码");
                }
                //判断sku编码不能为空
                if (StringUtils.isEmpty(item.getSpecNumber())) {
                    return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误：订单" + order.getId() + "没有SKU编码");
                }

                //查询商品信息
                var erpGoods = erpGoodsService.getGoodsEntityByNumber(item.getGoodsNumber());
                if (erpGoods == null) {
                    return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误：订单" + order.getId() + "商品编码:" + item.getGoodsNumber() + "不存在");
                }
                //查询商品规格
                var erpGoodsSpec = erpGoodsService.getSpecByNumber(item.getSpecNumber());
                if (erpGoodsSpec == null) {
                    return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "参数错误：订单" + order.getId() + "SKU编码:" + item.getSpecNumber() + "不存在");
                }

                //重新赋值
                item.setGoodsId(erpGoods.getId());
                item.setGoodsTitle(erpGoods.getName());

                item.setPrice(erpGoods.getWholesalePrice());
                item.setSpecId(erpGoodsSpec.getId());
                item.setGoodsImg(erpGoodsSpec.getColorImage());
                item.setColor(erpGoodsSpec.getColorValue());
                item.setSize(erpGoodsSpec.getSizeValue());

            }
        }

//        ResultVo<String> resultVo = salesOrderService.importExcelOrderForDaiFa(orderList, req.getBuyerUserId());

//        if (resultVo.getCode() == EnumResultVo.SUCCESS.getIndex()) {
//            return new ApiResult<>(0, "SUCCESS", resultVo.getData());
//        } else return new ApiResult<>(resultVo.getCode(), resultVo.getMsg());

        return new ApiResult<>(500, "未实现");
    }

}
