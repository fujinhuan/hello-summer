//package com.b2c.oms.controller.tao;
//
//
//import com.b2c.common.api.ApiResult;
//import com.b2c.common.api.ApiResultEnum;
//import com.b2c.entity.DataRow;
//import com.b2c.entity.datacenter.DcTmallOrderEntity;
//import com.b2c.entity.datacenter.DcTmallOrderRefundEntity;
//import com.b2c.entity.erp.ErpGoodsSpecEntity;
//import com.b2c.entity.result.EnumResultVo;
//import com.b2c.service.oms.AliOrderService;
//import com.b2c.service.oms.DcTmallOrderReturnService;
//import com.b2c.service.oms.DcTmallOrderService;
//import com.b2c.vo.oms.OrderRefundApplyItemVo;
//import com.b2c.vo.oms.OrderRefundApplyVo;
//import com.fasterxml.jackson.databind.exc.InvalidFormatException;
//import org.apache.poi.xssf.usermodel.XSSFRow;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//
///***
// * 淘系订单ajax
// */
//@RestController
//public class AjaxTaoOrderController {
//    private static Logger log = LoggerFactory.getLogger(AjaxTaoOrderController.class);
//    @Autowired
//    private DcTmallOrderService tmallOrderService;
//    @Autowired
//    private AliOrderService aliOrderService;
//    @Autowired
//    private DcTmallOrderReturnService tmallOrderReturnService;
//
//    /***
//     * 批量补充订单收货地址
//     * @param file
//     * @param req
//     * @return
//     * @throws IOException
//     * @throws InvalidFormatException
//     */
//    @RequestMapping(value = "/tao_order/set_order_receiver", method = RequestMethod.POST)
//    public ApiResult<Integer> setOrderReceiver(@RequestParam("excel") MultipartFile file, HttpServletRequest req) throws IOException, InvalidFormatException {
//
//        String fileName = file.getOriginalFilename();
//        String dir = System.getProperty("user.dir");
//        String destFileName = dir + File.separator + "uploadedfiles_" + fileName;
////        System.out.println(destFileName);
//        File destFile = new File(destFileName);
//        file.transferTo(destFile);
//        log.info("/***********批量导入收货地址信息，文件后缀" + destFileName.substring(destFileName.lastIndexOf(".")) + "***********/");
//
//        XSSFSheet sheet;
//        InputStream fis = null;
//
//        fis = new FileInputStream(destFileName);
//
//        //订单list
//        List<DcTmallOrderEntity> orderList = new ArrayList<>();
//
//        XSSFWorkbook workbook = null;
//        try {
//            workbook = new XSSFWorkbook(fis);
//
//            sheet = workbook.getSheetAt(0);
//
//            int lastRowNum = sheet.getLastRowNum();//最后一行索引
//            XSSFRow row = null;
//
//            //订单实体
//            DcTmallOrderEntity order = new DcTmallOrderEntity();
//            int currRowNum = 0;
//            for (int i = 1; i <= lastRowNum; i++) {
//                row = sheet.getRow(i);
//                currRowNum = i;
//                if (row != null) {
//
//                    //订单号
//                    String orderId = row.getCell(0).getStringCellValue();
//
//                    log.info("/***********批量导入收货地址信息***读取到订单ID:" + orderId + "***********/");
//                    if (StringUtils.isEmpty(orderId) == false) {
//                        order = new DcTmallOrderEntity();
//                        order.setId(orderId);
//
//
//                        //收货信息
//                        order.setContactPerson(row.getCell(2).getStringCellValue());
//                        order.setMobile(row.getCell(3).getStringCellValue());
//                        order.setProvince(row.getCell(5).getStringCellValue());
//                        order.setCity(row.getCell(6).getStringCellValue());
//                        order.setArea(row.getCell(7).getStringCellValue());
//                        order.setAddress(row.getCell(8).getStringCellValue());
//
//                        orderList.add(order);
//
//                    }
//
//
//                }
//            }
//
//            log.info("/***********批量导入收货地址信息***开始更新收货地址***********/");
//            tmallOrderService.batchUpdateTmallOrderReceiver(orderList);
//            return new ApiResult<>(0, "SUCCESS");
//
//        } catch (Exception ex) {
////            workbook = new HSSFWorkbook(fis);
//            log.info("/***********批量导入收货地址信息***出现异常：" + ex.getMessage() + "***********/");
//            return new ApiResult<>(500, ex.getMessage());
//        }
//
//    }
//
//
//    /**
//     * 修改商品规格(确认)
//     *
//     * @param data
//     * @param req
//     * @return
//     */
//    @RequestMapping(value = "/tao_order/upd_order_tmall_spec", method = RequestMethod.POST)
//    public ApiResult<Integer> updGoodSepc(@RequestBody DataRow data, HttpServletRequest req) {
//        Integer orderItemId = data.getInt("itemId");
//        Integer newSpecId = data.getInt("newSpecId");
//        if(newSpecId == 0)   return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "请选择新sku");
//
//        Integer result = tmallOrderService.updGoodTmallSpec(orderItemId,newSpecId );
//
//        if (result < 0) return new ApiResult<>(EnumResultVo.Fail.getIndex(), "修改失败");
//
//        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "修改成功");
//    }
//
//    /**
//     * 查询商品规格
//     *
//     * @param data
//     * @param req
//     * @return
//     */
//    @RequestMapping(value = "/tao_order/get_order_good_spec", method = RequestMethod.POST)
//    public ApiResult<List<ErpGoodsSpecEntity>> getGoodSpec(@RequestBody DataRow data, HttpServletRequest req) {
//        if (StringUtils.isEmpty(data.get("goodNumber")))
//            return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "商品编码不存在");
//        String goodsNumber = data.getString("goodNumber");
//        var result = aliOrderService.getGoodSpecByGoodNumber(goodsNumber);
//        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), "", result);
//
//    }
//
//
//    /**
//     * 天猫同意退货
//     *
//     * @param data
//     * @param req
//     * @return
//     */
//    @RequestMapping(value = "/tao_order/reviewRefund", method = RequestMethod.POST)
//    public ApiResult<String> reviewRefund(@RequestBody DataRow data, HttpServletRequest req) {
//
//        var result = tmallOrderReturnService.confirmTmallRefund(data.getLong("id"));
//
//        return new ApiResult<>(result.getCode(), result.getMsg());
//    }
//
//    /**
//     * 用户提交售后单
//     *
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/tao_order/refund_apply_submit", method = RequestMethod.POST)
//    public ApiResult<Integer> addOrderCancel(HttpServletRequest request) {
//        if (StringUtils.isEmpty(request.getParameter("order_id")))
//            return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误：缺少order_id");
//        if (StringUtils.isEmpty(request.getParameterValues("item[]")))
//            return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误：缺少item[]");
//        if(StringUtils.isEmpty(request.getParameter("refundId")))return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误：缺少退款单号");
//        if(StringUtils.isEmpty(request.getParameter("company")))return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误：缺少物流公司");
//        if(StringUtils.isEmpty(request.getParameter("code")))return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误：缺少物流单号");
//
//        synchronized (this) {
//            Integer shopId=7;
//            Long orderId = Long.parseLong(request.getParameter("order_id"));
//
//            //查询店铺信息
//            DcTmallOrderEntity orderDetail = tmallOrderService.getOrderDetailAndItemsById(orderId);
//            if (orderDetail == null) return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "订单不存在");
//
//            //组合退货参数
//            DcTmallOrderRefundEntity applyVo = new DcTmallOrderRefundEntity();
//            applyVo.setRefund_id(request.getParameter("refundId"));
//            applyVo.setTotal_fee(orderDetail.getTotalAmount().toString());
//            applyVo.setTid(Long.valueOf(orderDetail.getId()));
//            applyVo.setHas_good_return(1);//退款类型1:退货退款，0仅退款
//            applyVo.setLogisticsCode(request.getParameter("code"));
//            applyVo.setLogisticsCompany(request.getParameter("company"));
//            //退货商品list
//
//            String[] idDataArr = request.getParameterValues("item[]");
//            String[] nums = request.getParameterValues("count[]");
//
//            //循环查找选中的item
//            for (int i = 0; i < idDataArr.length; i++) {
//                String[] idData = idDataArr[i].split(":");
//                Long orderItemId = Long.parseLong(idData[1]);
//                Integer quantity = Integer.parseInt(nums[Integer.parseInt(idData[0])]);
//                //查找出orderItem并赋新SKUiD值
//                var orderItem = orderDetail.getItems().stream().filter(it -> it.getId().longValue() == orderItemId.longValue()).findFirst().get();
//                if (orderItem == null) return new ApiResult<>(EnumResultVo.ParamsError.getIndex(), "订单orderItem不存在");
//
//                applyVo.setNum(quantity.longValue());
//                applyVo.setRefund_fee(quantity*orderItem.getPrice().doubleValue()+"");
//                applyVo.setPayment(quantity*orderItem.getPrice().doubleValue()+"");
//                applyVo.setCreated(System.currentTimeMillis() / 1000);
//                applyVo.setStatus("WAIT_SELLER_CONFIRM_GOODS");
//                applyVo.setOid(Long.valueOf(orderItem.getSubItemId()));
//                break;
//            }
//            var result = tmallOrderReturnService.updOrderRefund(shopId, applyVo);
//            if (result == EnumResultVo.SUCCESS.getIndex()) {
//                return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(),"成功");
//            }else return new ApiResult<>(EnumResultVo.Fail.getIndex(),"失败");
//
//        }
//    }
//}
