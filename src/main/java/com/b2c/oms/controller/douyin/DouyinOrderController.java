package com.b2c.oms.controller.douyin;

import com.b2c.common.PagingResponse;
import com.b2c.common.utils.DateUtil;
import com.b2c.entity.douyin.DcDouyinOrdersEntity;
import com.b2c.entity.douyin.DcDouyinOrdersItemsEntity;
import com.b2c.entity.douyin.DcDouyinOrdersListVo;
import com.b2c.entity.erp.vo.GoodsSearchShowVo;
import com.b2c.entity.pdd.OrderPddEntity;
import com.b2c.entity.pdd.OrderPddItemEntity;
import com.b2c.enums.third.EnumDouYinOrderStatus;
import com.b2c.oms.DataConfigObject;
import com.b2c.repository.utils.OrderNumberUtils;
import com.b2c.service.ShopService;
import com.b2c.service.erp.ErpGoodsService;
import com.b2c.service.oms.DouyinOrderService;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 描述：
 *
 * @author qlp
 * @date 2019-11-13 14:44
 */
@Controller
@RequestMapping("/douyin")
public class DouyinOrderController {
    @Autowired
    private ShopService shopService;
    @Autowired
    private DouyinOrderService douyinOrderService;
    @Autowired
    private ErpGoodsService erpGoodsService;

    /**
     * 订单列表
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("/order_list")
    public String orderList(Model model, HttpServletRequest request,@RequestParam Integer shopId){
        //Integer shopId= 8;
        //查询店铺信息
        var shop = shopService.getShop(shopId);
        model.addAttribute("shop",shop);

        model.addAttribute("menuId","order_list");

        String orderNum="";
        if (!StringUtils.isEmpty(request.getParameter("orderNum"))) {
            orderNum = request.getParameter("orderNum");
            model.addAttribute("orderNum", orderNum);
        }
        String status="";
        if (!StringUtils.isEmpty(request.getParameter("state"))) status = request.getParameter("state");
        String logisticsCode="";
        if (!StringUtils.isEmpty(request.getParameter("logisticsCode"))){
            logisticsCode = request.getParameter("logisticsCode");
            model.addAttribute("logisticsCode",logisticsCode);
        }
        Integer startTime=null;
        if (!StringUtils.isEmpty(request.getParameter("startTime"))){
            DateUtil.dateTimeToStamp(request.getParameter("startTime"));
            model.addAttribute("startTime",request.getParameter("startTime"));
        }
        Integer endTime=null;
        if (!StringUtils.isEmpty(request.getParameter("endTime"))){
            String endTimes=request.getParameter("endTime");
            if(endTimes.indexOf("00:00:00")>0){
                endTimes=endTimes.replace("00:00:00","23:59:59");
            }
            endTime = DateUtil.dateTimeToStamp(endTimes);
            model.addAttribute("endTime",endTimes);
        }

        Integer pageIndex = 1, pageSize = DataConfigObject.getInstance().getPageSize();
        if (!StringUtils.isEmpty(request.getParameter("page"))) {
            pageIndex = Integer.parseInt(request.getParameter("page"));
        }
        PagingResponse<DcDouyinOrdersListVo> result = douyinOrderService.getDouyinOrders(shop.getSellerUserId(),pageIndex,pageSize,orderNum,startTime,endTime,status,logisticsCode);

        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalSize", result.getTotalSize());
        model.addAttribute("lists", result.getList());
        model.addAttribute("shopId",shop.getId());

        return "order/douyin/order_list_douyin";
    }



    @ResponseBody
    @RequestMapping(value = "/order_list_douyin_export")
    public void erpStockOutItemRefundInvoiceExport(HttpServletRequest req, HttpServletResponse response) throws Exception {

        Integer startTime=StringUtils.isEmpty(req.getParameter("startTime")) ? null : DateUtil.dateTimeToStamp(req.getParameter("startTime"));
        Integer endTime=null;
        if(!StringUtils.isEmpty(req.getParameter("endTime"))){
            String endTimes=req.getParameter("endTime");
            if(endTimes.indexOf("00:00:00")>0){
                endTimes=endTimes.replace("00:00:00","23:59:59");
            }
            endTime = DateUtil.dateTimeToStamp(endTimes);
        }

        var lists = douyinOrderService.getDouyinOrdersExport(startTime,endTime,null);

        /***************根据店铺查询订单导出的信息*****************/
        String fileName = "order_list_douyin_export_";//excel文件名前缀
        //创建Excel工作薄对象
        HSSFWorkbook workbook = new HSSFWorkbook();

        //创建Excel工作表对象
        HSSFSheet sheet = workbook.createSheet();
        HSSFRow row = null;
        HSSFCell cell = null;
        //第一行为空
        row = sheet.createRow(0);
        cell = row.createCell(0);
        cell.setCellValue("");
        cell = row.createCell(0);
        cell.setCellValue("订单号");
        cell = row.createCell(1);
        cell.setCellValue("商品编码");
        cell = row.createCell(2);
        cell.setCellValue("规格编码");
        cell = row.createCell(3);
        cell.setCellValue("数量");
        cell = row.createCell(4);
        cell.setCellValue("成本");
        cell = row.createCell(5);
        cell.setCellValue("总价");
        cell = row.createCell(6);
        cell.setCellValue("日期");
        cell = row.createCell(7);
        cell.setCellValue("主播ID");
        cell = row.createCell(8);
        cell.setCellValue("主播名称");

        int currRowNum = 0;
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
        // 循环写入数据
        for (int i = 0; i < lists.size(); i++) {
            currRowNum++;
            //写入订单
            DcDouyinOrdersItemsEntity itemVo = lists.get(i);
            //创建行
            row = sheet.createRow(currRowNum);
            cell = row.createCell(0);
            cell.setCellValue(itemVo.getOrderId());
            //商品名称
            cell = row.createCell(1);
            cell.setCellValue(itemVo.getGoodsNumber());
            cell = row.createCell(2);
            cell.setCellValue(itemVo.getCode());

            cell = row.createCell(3);
            cell.setCellValue(itemVo.getComboNum());

/*            HSSFClientAnchor anchor=new HSSFClientAnchor(0,0,40,40,(short)4,currRowNum,(short)4,currRowNum);

            URL url = new URL(itemVo.getProductPic());
            BufferedImage bufferImg = ImageIO.read(url);
            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
            ImageIO.write(bufferImg, "jpg", byteArrayOut);
            byte[] data = byteArrayOut.toByteArray();

            patriarch.createPicture(anchor, workbook.addPicture(data, HSSFWorkbook.PICTURE_TYPE_JPEG));*/
            //退货单号
            cell = row.createCell(4);
            cell.setCellValue(StringUtils.isEmpty(itemVo.getCouponAmount()) ? 0 : itemVo.getCouponAmount());
            cell = row.createCell(5);
            cell.setCellValue(itemVo.getTotalAmount());
            //规格
            cell = row.createCell(6);
            cell.setCellValue(itemVo.getPrintDate());
            cell = row.createCell(7);
            cell.setCellValue(itemVo.getAuthorId());
            cell = row.createCell(8);
            cell.setCellValue(itemVo.getAuthorName());
        }
        response.reset();
        response.setContentType("application/msexcel;charset=UTF-8");
        try {
            SimpleDateFormat newsdf = new SimpleDateFormat("yyyyMMdd");
            String date = newsdf.format(new Date());
            response.addHeader("Content-Disposition", "attachment;filename=\""
                    + new String((fileName + date + ".xls").getBytes("GBK"),
                    "ISO8859_1") + "\"");
            OutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "导出失败!");
            e.printStackTrace();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "导出失败!");
            e.printStackTrace();
        }
    }

    /**
     * 订单详情（douyin）
     *
     * @param model
     * @param id
     * @param shopId
     * @param request
     * @return
     */
    @RequestMapping(value = "/order_detail", method = RequestMethod.GET)
    public String orderDetailTmall(Model model, @RequestParam Long id, @RequestParam Integer shopId, HttpServletRequest request) {

        DcDouyinOrdersListVo orderDetail = douyinOrderService.getOderDetailByOrderId(id);

        model.addAttribute("orderVo", orderDetail);

        //查询店铺信息
        var shop = shopService.getShop(shopId);
        model.addAttribute("shop", shop);
        model.addAttribute("menuId", "order_list");

        return "order/douyin/order_detail_douyin";
    }
    /**
     * 订单确认页面
     * @param model
     * @param orderId
     * @param request
     * @return
     */
    @RequestMapping("/order_confirm")
    public String orderList(Model model,@RequestParam Long orderId, HttpServletRequest request){
        DcDouyinOrdersListVo orderDetail = douyinOrderService.getOderDetailByOrderId(orderId);
        model.addAttribute("orderVo", orderDetail);

        return "order/douyin/order_confirm_douyin";
    }

    /**
     * 创建订单
     * @param model
     * @param shopId
     * @param request
     * @return
     */
    @RequestMapping("/order_create")
    public String orderCreate(Model model,@RequestParam Integer shopId, HttpServletRequest request){
        model.addAttribute("orderNumber", OrderNumberUtils.getOrderIdByTime());
        model.addAttribute("menuId", "order_create");
        return "v3/order_create_douyin";
    }

    /**
     * 创建订单 post
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/order_create", method = RequestMethod.POST)
    public String orderCreate(Model model, HttpServletRequest request){
        model.addAttribute("menuId", "order_create");
        /***商品信息****/
        String[] specNumber = request.getParameterValues("specNumber");//规格编码组合
        String[] goodsNumber = request.getParameterValues("goodsNumber");//商品编码组合
        String[] goodsId = request.getParameterValues("goodsId");//商品id组合
        String[] specsId = request.getParameterValues("specId");//商品规格id组合
        String[] quantitys = request.getParameterValues("quantity");//数量组合
        String[] prices = request.getParameterValues("note");//商品价格

        String orderNumber = request.getParameter("orderNumber");
        //收件人信息
        String contactMobile = request.getParameter("contactMobile");
        String contactPerson = request.getParameter("contactPerson");
        String area = request.getParameter("area");
        String address = request.getParameter("address");
        Integer saleType = Integer.parseInt(request.getParameter("saleType"));
        String shippingFee= StringUtils.isEmpty(request.getParameter("shippingFee")) ? "0" :request.getParameter("shippingFee");
        String sellerMemo = request.getParameter("sellerMemo");

        String[] areaNameArray = area.split(" ");

        String provinceName = "";
        if (areaNameArray.length > 0) provinceName = areaNameArray[0];
        String cityName = "";
        if (areaNameArray.length > 1) cityName = areaNameArray[1];
        String districtName = "";
        if (areaNameArray.length > 2) districtName = areaNameArray[2];

        DcDouyinOrdersEntity order = new DcDouyinOrdersEntity();

        List<DcDouyinOrdersItemsEntity> items = new ArrayList<>();
        double goodsTotalAmount = 0;//商品总价
        for (int i = 0,n=goodsId.length;i<n;i++) {
            if(StringUtils.isEmpty(goodsId[i]))continue;
            DcDouyinOrdersItemsEntity item = new DcDouyinOrdersItemsEntity();
            Integer specId=Integer.parseInt(specsId[i]);
            BigDecimal price = new BigDecimal(prices[i]);
            Integer count =Integer.parseInt(quantitys[i]);
            var spec = erpGoodsService.getSpecBySpecId(specId);

            goodsTotalAmount +=  price.doubleValue() * count;
            double itemTotalAmount=price.doubleValue() * count;
            item.setOrderId(orderNumber);
            item.setComboId(0L);
            item.setCouponAmount(0d);
            item.setCouponMetaId(0L);
            item.setIsComment(0);
            item.setShopId(8L);
            item.setPid(orderNumber);
            item.setPostAmount(0d);
            item.setGoodsNumber(goodsNumber[i]);
            item.setCode(specNumber[i]);
            item.setComboNum(count);
            item.setTotalAmount(itemTotalAmount*100);
            item.setErpGoodsId(spec.getGoodsId());
            item.setErpGoodsSpecId(spec.getId());
            item.setProductId(0L);
            item.setSkuInfo(spec.getColorValue()+","+spec.getSizeValue());
            item.setSpecDesc(spec.getColorValue()+","+spec.getSizeValue());
            item.setProductPic(spec.getColorImage());
            item.setProductName(spec.getGoodTitle());
            item.setPrice(price.doubleValue());
            item.setIsGift(saleType.intValue()==0 ? 1 : 0);
            items.add(item);
        }
        order.setSkuOrderList(items);
        order.setPostAddr(new StringBuilder(provinceName).append(cityName).append(districtName).append(address).toString());
        order.setPostTel(contactMobile);
        order.setPostReceiver(contactPerson);
        order.setSellerWords(sellerMemo);
        order.setOrderId(orderNumber);
        order.setCreateTime(System.currentTimeMillis() / 1000);
        order.setShopId(8L);
        order.setOrderTotalAmount(goodsTotalAmount*100);
        order.setOrderStatus(EnumDouYinOrderStatus.WAIT_SEND_GOODS.getThirdIndex());
        order.setPostAmount(Double.valueOf(shippingFee)*100);
        order.setLogisticsId("");
        order.setLogisticsTime(0L);
        order.setReceiptTime(0L);
        order.setExpShipTime(0L);
        order.setUpdateTime(0L);
        order.setPayType(0);
        order.setPayTime(0L);
        order.setCouponAmount(0d);
        order.setShopCouponAmount(0d);
        order.setIsComment(0);
        order.setUrgeCnt(0);
        order.setcType(0);
        order.setbType(0);
        order.setCosRatio(new BigDecimal(0));
        douyinOrderService.editDouYinOrder(order,0);
        return "redirect:/douyin/order_list?shopId=8";
    }

    @RequestMapping("/order_print_list")
    public String order_print_list(Model model, HttpServletRequest request,@RequestParam Integer shopId){
        //Integer shopId= 8;
        //查询店铺信息
        var shop = shopService.getShop(shopId);
        model.addAttribute("shop",shop);

        Integer print=0;
        if (!StringUtils.isEmpty(request.getParameter("print"))) {
            print =Integer.parseInt(request.getParameter("print"));
            model.addAttribute("printStatus", print);
        }

        model.addAttribute("menuId","order_list");

        String orderNum="";
        if (!StringUtils.isEmpty(request.getParameter("orderNum"))) {
            orderNum = request.getParameter("orderNum");
            model.addAttribute("orderNum", orderNum);
        }

        String goodsNum="";
        if (!StringUtils.isEmpty(request.getParameter("goodsNum"))) {
            goodsNum = request.getParameter("goodsNum");
            model.addAttribute("goodsNum", goodsNum);
        }

        String goodsSpecNum="";
        if (!StringUtils.isEmpty(request.getParameter("goodsSpecNum"))) {
            goodsSpecNum = request.getParameter("goodsSpecNum");
            model.addAttribute("goodsSpecNum", goodsSpecNum);
        }

        Integer startTime=null;
        if (!StringUtils.isEmpty(request.getParameter("startTime"))) DateUtil.dateTimeToStamp(request.getParameter("startTime")); model.addAttribute("startTime",startTime);
        Integer endTime=null;
        if (!StringUtils.isEmpty(request.getParameter("endTime"))) DateUtil.dateTimeToStamp(request.getParameter("endTime")); model.addAttribute("endTime",endTime);

        Integer pageIndex = 1, pageSize = 150;
        if (!StringUtils.isEmpty(request.getParameter("page"))) {
            pageIndex = Integer.parseInt(request.getParameter("page"));
        }


        String trackingNumber="";
        if (!StringUtils.isEmpty(request.getParameter("trackingNumber"))) {
            trackingNumber = request.getParameter("trackingNumber");
            model.addAttribute("trackingNumber", trackingNumber);
        }
        String printStartTime="";
        if (!StringUtils.isEmpty(request.getParameter("printStartTime"))) {
            printStartTime = request.getParameter("printStartTime");
            model.addAttribute("printStartTime", printStartTime);
        }
        String printEndTime="";
        if (!StringUtils.isEmpty(request.getParameter("printEndTime"))) {
            printEndTime = request.getParameter("printEndTime");
            model.addAttribute("printEndTime", printEndTime);
        }
        Integer isBz=-1;
        if (!StringUtils.isEmpty(request.getParameter("isBz"))) {
            isBz = Integer.parseInt(request.getParameter("isBz"));
            model.addAttribute("isBz", isBz);
        }

        PagingResponse<DcDouyinOrdersItemsEntity> result = douyinOrderService.getDouyinPrintOrders(shop.getSellerUserId().intValue(),pageIndex,pageSize,orderNum,startTime,endTime,goodsNum,goodsSpecNum,print,trackingNumber,printStartTime,printEndTime,isBz);

        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalSize", result.getTotalSize());
        model.addAttribute("lists", result.getList());
        model.addAttribute("shopId",shop.getId());
        model.addAttribute("hb",0);

        if(print==0){
            return "order/douyin/order_douyin_code";
        }else if(print==1){
            return "order/douyin/order_douyin_print";
        }else if(print==2){
            return "order/douyin/order_douyin_send";
        }else if(print==4){
        return "order/douyin/order_douyin_code_error";
        }

        return "order/douyin/order_douyin_print";

    }

    @RequestMapping("/order_douyin_code_hebing")
    public String orderDouyinCodeHebing(Model model, HttpServletRequest request,@RequestParam Integer shopId){
        var shop = shopService.getShop(shopId);
        Integer print=0;
        if (!StringUtils.isEmpty(request.getParameter("print"))) {
            print =Integer.parseInt(request.getParameter("print"));
            model.addAttribute("printStatus", print);
        }
        List<DcDouyinOrdersListVo> result = douyinOrderService.getDouyinOrderHebing(shop.getSellerUserId().intValue());

        model.addAttribute("lists", result);
        model.addAttribute("hb",1);
        model.addAttribute("shopId",shop.getId());

        return "order/douyin/order_douyin_code_hebing";

    }


    @ResponseBody
    @RequestMapping(value = "/order_list_douyin_sku_export")
    public void orderListDouyinSkuExport(HttpServletRequest req, HttpServletResponse response) throws Exception {

        Integer startTime=StringUtils.isEmpty(req.getParameter("startTime")) ? null : DateUtil.dateToStamp(req.getParameter("startTime"));
        Integer endTime=StringUtils.isEmpty(req.getParameter("endTime")) ? null : DateUtil.dateToStamp(req.getParameter("endTime")+" 23:59:59");
/*        if(!StringUtils.isEmpty(req.getParameter("endTime"))){
            String endTimes=req.getParameter("endTime")+;
            if(endTimes.indexOf("00:00:00")>0){
                endTimes=endTimes.replace("00:00:00","23:59:59");
            }
            endTime = DateUtil.dateTimeToStamp(endTimes);
        }*/

        var result = erpGoodsService.getDySalesList(2148336L,1,1000,null,startTime,endTime);

        List<GoodsSearchShowVo> lists=result.getList();


        /***************根据店铺查询订单导出的信息*****************/
        String fileName = "order_dy_sku_"+req.getParameter("startTime")+"_"+req.getParameter("endTime");//excel文件名前缀
        //创建Excel工作薄对象
        HSSFWorkbook workbook = new HSSFWorkbook();

        //创建Excel工作表对象
        HSSFSheet sheet = workbook.createSheet();
        HSSFRow row = null;
        HSSFCell cell = null;
        //第一行为空
        row = sheet.createRow(0);
        cell = row.createCell(0);
        cell.setCellValue("");
        cell = row.createCell(0);
        cell.setCellValue("商品编码");
        cell = row.createCell(1);
        cell.setCellValue("规格编码");
        cell = row.createCell(2);
        cell.setCellValue("销量");
        cell = row.createCell(3);
        cell.setCellValue("价格");

        int currRowNum = 0;
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
        // 循环写入数据
        for (int i = 0; i < lists.size(); i++) {
            currRowNum++;
            //写入订单
            GoodsSearchShowVo itemVo = lists.get(i);
            //创建行
            row = sheet.createRow(currRowNum);
            cell = row.createCell(0);
            cell.setCellValue(itemVo.getGoodsNumber());
            //商品名称
            cell = row.createCell(1);
            cell.setCellValue(itemVo.getSpecNumber());
            cell = row.createCell(2);
            cell.setCellValue(itemVo.getQuantity());

            cell = row.createCell(3);
            cell.setCellValue(itemVo.getSalePrice());

/*            HSSFClientAnchor anchor=new HSSFClientAnchor(0,0,40,40,(short)4,currRowNum,(short)4,currRowNum);

            URL url = new URL(itemVo.getProductPic());
            BufferedImage bufferImg = ImageIO.read(url);
            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
            ImageIO.write(bufferImg, "jpg", byteArrayOut);
            byte[] data = byteArrayOut.toByteArray();

            patriarch.createPicture(anchor, workbook.addPicture(data, HSSFWorkbook.PICTURE_TYPE_JPEG));*/
            //退货单号
        }
        response.reset();
        response.setContentType("application/msexcel;charset=UTF-8");
        try {
            SimpleDateFormat newsdf = new SimpleDateFormat("yyyyMMdd");
            String date = newsdf.format(new Date());
            response.addHeader("Content-Disposition", "attachment;filename=\""
                    + new String((fileName + ".xls").getBytes("GBK"),
                    "ISO8859_1") + "\"");
            OutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "导出失败!");
            e.printStackTrace();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "导出失败!");
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/douyin_order_sales_statis", method = RequestMethod.GET)
    public String list(Model model, HttpServletRequest request,@RequestParam Integer shopId) {
        //var shop = shopService.getShop(shopId);
        String startTime=DateUtil.customizeDate_(7);
        if (!StringUtils.isEmpty(request.getParameter("startTime"))){
            startTime=request.getParameter("startTime");

        }
        model.addAttribute("startTime",startTime);
        String endTime=DateUtil.getCurrentDate();
        if (!StringUtils.isEmpty(request.getParameter("endTime"))){
             endTime=request.getParameter("endTime");
        }
        model.addAttribute("endTime",endTime);

        model.addAttribute("shopId",shopId);

        var result = douyinOrderService.douyinOrderStatis(startTime,endTime);

        model.addAttribute("menuId", "order_count");
        model.addAttribute("lists", result);

        return "douyin_order_sales_statis";
    }

}
