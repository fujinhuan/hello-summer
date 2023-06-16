package com.b2c.oms.controller.ajax;

import com.b2c.common.api.ApiResult;
import com.b2c.common.utils.DateUtil;
import com.b2c.entity.result.EnumResultVo;
import com.b2c.entity.result.ResultVo;
import com.b2c.enums.erp.EnumErpOrderStatus;
import com.b2c.oms.request.PiPiOrderImportSubmitReq;
import com.b2c.service.erp.ErpGoodsService;
import com.b2c.service.erp.ErpSalesOrderService;
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

@RequestMapping("/sales")
@RestController
public class SalesOrderImportAjaxPipiController {
    @Autowired
    private ErpGoodsService erpGoodsService;
    @Autowired
    private ErpSalesOrderService salesOrderService;

    private static Logger log = LoggerFactory.getLogger(SalesOrderImportAjaxPipiController.class);


}
