package com.b2c.oms.controller;

import com.b2c.common.api.ApiResult;
import com.b2c.common.api.ApiResultEnum;
import com.b2c.common.PagingResponse;
import com.b2c.common.utils.DateUtil;
import com.b2c.common.utils.HttpImageUtil;
import com.b2c.entity.OrderItemEntity;
import com.b2c.entity.UserEntity;
import com.b2c.entity.datacenter.DcAliOrderListVo;
import com.b2c.entity.datacenter.vo.OfflineOrderListVo;
import com.b2c.entity.enums.OrderTypeEnums;
import com.b2c.entity.result.ResultVo;
import com.b2c.entity.vo.OrderVo;
import com.b2c.oms.DataConfigObject;
import com.b2c.oms.request.OrderConfirmReq;
import com.b2c.service.oms.OrderYungouService;
import com.b2c.service.ShopService;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：
 *
 * @author qlp
 * @date 2019-09-24 16:05
 */
@RequestMapping("/order_yungou")
@Controller
public class YGOrderController {
    @Autowired
    private ShopService shopService;
    @Autowired
    private OrderYungouService orderYungouService;
//    @Autowired
//    private UserService userService;

    /**
     * 订单列表
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String orderList(Model model, @RequestParam Integer shopId, HttpServletRequest request) {
        //订单类型参数
        OrderTypeEnums orderTypeEnums = OrderTypeEnums.PT;

        if (shopId.intValue() == 3) {
            orderTypeEnums = OrderTypeEnums.PT;
        } else if (shopId.intValue() == 4) {
            orderTypeEnums = OrderTypeEnums.PiFa;

        } else if (shopId.intValue() == 11) {
            orderTypeEnums = OrderTypeEnums.DaiFa;
        }

        //查询店铺信息
        var shop = shopService.getShop(shopId);
        model.addAttribute("shop", shop);
        model.addAttribute("shopName", shop.getName());
        model.addAttribute("menuId", "order_list");


        String page = request.getParameter("page");
        Integer pageIndex = 1;
        Integer pageSize = DataConfigObject.getInstance().getPageSize();
        String orderNum = "";
        String mjMobile = "";
        Integer startTime = 0;
        Integer endTime = 0;
        String salesmanMobile = "";
//        String developerMobile = "";
        Integer state = -1;

        if (!StringUtils.isEmpty(request.getParameter("status"))) {
            state = Integer.parseInt(request.getParameter("status"));
        }
        if (!StringUtils.isEmpty(page)) {
            pageIndex = Integer.parseInt(page);
        }
        if (!StringUtils.isEmpty(request.getParameter("pageSize"))) {
            try {
                pageSize = Integer.parseInt(request.getParameter("pageSize"));
            } catch (Exception e) {
            }
        }
        if (!StringUtils.isEmpty(request.getParameter("orderNum"))) {
            orderNum = request.getParameter("orderNum");
        }
        if (!StringUtils.isEmpty(request.getParameter("mjMobile"))) {
            mjMobile = request.getParameter("mjMobile");
        }
        if (!StringUtils.isEmpty(request.getParameter("startTime"))) {
            startTime = DateUtil.dateToStamp(request.getParameter("startTime"));
        }
        if (!StringUtils.isEmpty(request.getParameter("endTime"))) {
            endTime = DateUtil.dateTimeToStamp(request.getParameter("endTime") + " 23:59:59");
        }
//        if (!StringUtils.isEmpty(request.getParameter("goodsName"))) {
//            goodsName = request.getParameter("goodsName");
//        }
        if (!StringUtils.isEmpty(request.getParameter("salesmanMobile"))) {
            salesmanMobile = request.getParameter("salesmanMobile");
        }

        Integer developerId = 0;
        if (!StringUtils.isEmpty(request.getParameter("developerId"))) {
            developerId = Integer.parseInt(request.getParameter("developerId"));
        }
        model.addAttribute("developerId", developerId);
//        if (!StringUtils.isEmpty(request.getParameter("developerMobile"))) {
//            developerMobile = request.getParameter("developerMobile");
//        }
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("pageSize", pageSize);
        PagingResponse<OrderVo> result = orderYungouService.getOrders(pageIndex, pageSize, orderNum, mjMobile, startTime, endTime, salesmanMobile, developerId, state, orderTypeEnums);
        model.addAttribute("totalSize", result.getTotalSize());
        model.addAttribute("lists", result.getList());

        //查询业务员
        List<UserEntity> developerList = new ArrayList<>();//userService.getDeveloperList();
        model.addAttribute("developerList", developerList);

        return "v3/yg/order_list_yungou";
    }

    /**
     * 订单详情
     *
     * @param model
     * @param id      订单Id
     * @param request
     * @return
     */
    @RequestMapping(value = "/order_detail", method = RequestMethod.GET)
    public String getOrderDetail(Model model, @RequestParam Long id, @RequestParam Integer shopId, HttpServletRequest request) {
        OfflineOrderListVo orderDetail = orderYungouService.getOrderDetail(id);

//        if (orderDetail.getType().intValue() == OrderTypeEnums.PiFa.getIndex()) {
//            shopId = 4;
//        } else if (orderDetail.getType().intValue() == OrderTypeEnums.DaiFa.getIndex()) {
//            shopId = 11;
//        }
        //查询店铺信息
        var shop = shopService.getShop(shopId);
        model.addAttribute("shop", shop);
        model.addAttribute("shopName", shop.getName());
        model.addAttribute("menuId", "order_list");


//        Integer state = dcOrderService.getOrderState(id);
        model.addAttribute("order", orderDetail);
//        model.addAttribute("state", state);  //状态，数字

//        String param = "{\"com\":\"" + orderDetail.getSendCompany() + "\",\"num\":\"" + orderDetail.getSendCode() + "\",\"\":\"" + orderDetail.getConsigneeMobile() + "\"}";
//        String customer = "B420E04416B8E902204B30C368B409DB";
//        String key = "HemHbdOY1667";
//        String sign = Md5Util.MD5(param + key + customer);// MD5.encode(param+key+customer);
//        HashMap params = new HashMap();
//        params.put("param", param);
//        params.put("sign", sign);
//        params.put("customer", customer);
//        String resp;
//        List<LogisticsVo> data = null;
//        try {
//            resp = new HttpRequest().httpRequest("http://poll.kuaidi100.com/poll/query.do", params);
//            QueryResultVo queryResultVo = JSONObject.parseObject(resp, QueryResultVo.class);
//            data = queryResultVo.getData();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        model.addAttribute("data", data);
        return "v3/yg/order_detail_yungou";
    }

    /**
     * 下载订单详情
     *
     * @param request
     * @param id       订单ID
     * @param response
     */
    @RequestMapping(value = "/order_detail_down", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public void outExport(HttpServletRequest request, @RequestParam Long id, HttpServletResponse response) {

        OfflineOrderListVo orderDetail = orderYungouService.getOrderDetail(id);


        //创建Excel工作薄对象
        HSSFWorkbook workbook = new HSSFWorkbook();

        //创建Excel工作表对象
        HSSFSheet sheet = workbook.createSheet();
        HSSFRow row = null;
        HSSFCell cell = null;
        //第一行为空

        //第二行收货信息
        StringBuilder sb = new StringBuilder();
        sb.append("买家手机：\r\n");
        sb.append("收货人：" + orderDetail.getConsignee() + "\r\n");
        sb.append("收货人手机号：" + orderDetail.getConsigneeMobile() + "\r\n");
        sb.append("收货地址：" + orderDetail.getAddress() + "\r\n");

        row = sheet.createRow(1);
        // 设置表头字体样式
        HSSFFont columnHeadFont = workbook.createFont();
        columnHeadFont.setFontName("宋体");
        columnHeadFont.setFontHeightInPoints((short) 10);

        // 列头的样式
        HSSFCellStyle cellStyle = workbook.createCellStyle();
//        columnHeadStyle.setFont(columnHeadFont);
        // 上下居中

        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);


        // 设置行高
        row.setHeight((short) 2000);

        cell = row.createCell(0);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(sb.toString());

        //合并单元格
        CellRangeAddress region = new CellRangeAddress(1, 1, 0, 5);
        sheet.addMergedRegion(region);


        //第三行订单明细表头
        row = sheet.createRow(2);

        cell = row.createCell(0);
        cell.setCellValue("产品图示");

        cell = row.createCell(1);
        cell.setCellValue("商品名称");

        cell = row.createCell(2);
        cell.setCellValue("产品款号");

        cell = row.createCell(3);
        cell.setCellValue("产品规格");

        cell = row.createCell(4);
        cell.setCellValue("产品SKU");

        cell = row.createCell(5);
        cell.setCellValue("单价");

        cell = row.createCell(6);
        cell.setCellValue("数量");

        cell = row.createCell(7);
        cell.setCellValue("总价");

        cell = row.createCell(8);
        cell.setCellValue("备注");

        // 循环写入数据
        for (int i = 0; i < orderDetail.getItems().size(); i++) {

            OrderItemEntity item = orderDetail.getItems().get(i);
            int rowNum = i + 3;

            //创建行
            row = sheet.createRow(rowNum);
            // 设置行高
            row.setHeight((short) 1800);

            /*********商品图片************/
            int cellNum = 0;
            cell = row.createCell(cellNum);

            //读取商品图片
            if (StringUtils.isEmpty(item.getImage()) == false) {
                byte[] outByteArray = HttpImageUtil.getImageFromNetByUrl(item.getImage());

                HSSFPatriarch patriarch = sheet.createDrawingPatriarch();

                /**
                 * 该构造函数有8个参数
                 * 前四个参数是控制图片在单元格的位置，分别是图片距离单元格left，top，right，bottom的像素距离
                 * 后四个参数，前两个表示图片左上角所在的cellNum和 rowNum，后天个参数对应的表示图片右下角所在的cellNum和 rowNum，
                 * excel中的cellNum和rowNum的index都是从0开始的
                 *
                 */
                HSSFClientAnchor anchor1 = new HSSFClientAnchor(0, 0, 0, 0, (short) cellNum, rowNum, (short) (cellNum + 1), rowNum + 1);
                patriarch.createPicture(anchor1, workbook.addPicture(outByteArray, HSSFWorkbook.PICTURE_TYPE_JPEG));
            }
            //商品标题
            cell = row.createCell(1);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(item.getTitle());

            //商品款号
            cell = row.createCell(2);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(item.getGoodsNumber());

            //商品规格
            cell = row.createCell(3);
            cell.setCellStyle(cellStyle);
            cell.setCellValue("颜色：" + item.getColor() + "  尺码：" + item.getSize());

            //商品SKU
            cell = row.createCell(4);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(item.getSpecNumber());

            //单价
            cell = row.createCell(5);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(item.getPrice().doubleValue());

            //数量
            cell = row.createCell(6);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(item.getCount() + "");

            //总价
            cell = row.createCell(7);
            cell.setCellStyle(cellStyle);
            Double totalPrice = item.getPrice().doubleValue() * item.getCount();
            cell.setCellValue(totalPrice);


        }
        response.reset();
        response.setContentType("application/msexcel;charset=UTF-8");
        try {
//            SimpleDateFormat newsdf=new SimpleDateFormat("yyyyMMddHHmmss");
//            String date = newsdf.format(new Date());
            response.addHeader("Content-Disposition", "attachment;filename=\""
                    + new String(("" + orderDetail.getOrderNum() + ".xls").getBytes("GBK"),
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

        return;
    }


    /**
     * 订单确认
     *
     * @param model
     * @param orderId
     * @param request
     * @return
     */
    @RequestMapping(value = "/order_confirm", method = RequestMethod.GET)
    public String orderConfirmGet(Model model, @RequestParam Long orderId, HttpServletRequest request) {
        OfflineOrderListVo order = orderYungouService.getOrderDetail(orderId);
        if (order == null) {
            model.addAttribute("error", "没有找到订单");
            model.addAttribute("orderVo", new DcAliOrderListVo());

        } else {
            model.addAttribute("orderVo", order);
        }

        return "v3/yg/order_confirm_yungou";
    }

    /**
     * 确认订单
     *
     * @param req
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/order_confirm_post", method = RequestMethod.POST)
    public ApiResult<Integer> confirmOrderYg(@RequestBody OrderConfirmReq req) {
        if (req.getOrderId() == null || req.getOrderId() <= 0)
            return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误，缺少orderId");
        if (StringUtils.isEmpty(req.getReceiver()))
            return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误，缺少receiver");
        if (StringUtils.isEmpty(req.getMobile()))
            return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误，缺少mobile");
        if (StringUtils.isEmpty(req.getAddress()))
            return new ApiResult<>(ApiResultEnum.ParamsError.getIndex(), "参数错误，缺少address");

        ResultVo<Integer> result = orderYungouService.orderConfirmAndJoinDeliveryQueueForYunGou(req.getOrderId(), req.getReceiver(), req.getMobile(), req.getAddress(), req.getSellerMemo());
        return new ApiResult<>(result.getCode(), result.getMsg());
    }
}