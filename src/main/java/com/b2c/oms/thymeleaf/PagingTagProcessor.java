package com.b2c.oms.thymeleaf;

import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.WebEngineContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring5.context.SpringContextUtils;
import org.thymeleaf.templatemode.TemplateMode;

public class PagingTagProcessor extends AbstractElementTagProcessor {

    private static final String TAG_NAME = "pager";//标签名
    private static final int PRECEDENCE = 10000;//优先级

    public PagingTagProcessor(String dialectPrefix) {

//        super(templateMode, dialectPrefix, elementName, prefixElementName, attributeName, prefixAttributeName, precedence);

//        super(
//                TemplateMode.HTML, // 此处理器将仅应用于HTML模式
//                dialectPrefix,     // 要应用于名称的匹配前缀
//                TAG_NAME,          // 标签名称：匹配此名称的特定标签
//                true,              // 将标签前缀应用于标签名称
//                null,              // 无属性名称：将通过标签名称匹配
//                false,             // 没有要应用于属性名称的前缀
//                PRECEDENCE);       // 优先(内部方言自己的优先)


        super(
                TemplateMode.HTML, // 此处理器将仅应用于HTML模式
                dialectPrefix,     // 要应用于名称的匹配前缀
                TAG_NAME,              // 无标签名称：匹配任何标签名称
                true,             // 没有要应用于标签名称的前缀
                null,         // 将匹配的属性的名称
                false,              // 将方言前缀应用于属性名称
                PRECEDENCE        // 优先(内部方言自己的优先)
        );

    }

    @Override
    protected void doProcess(ITemplateContext context,
                             IProcessableElementTag tag,
                             IElementTagStructureHandler structureHandler) {

        String url = ((WebEngineContext) context).getRequest().getRequestURI().toString();
        String queryString = ((WebEngineContext) context).getRequest().getQueryString();
        String pageUrl = "";
        if (StringUtils.isEmpty(queryString)) {
            pageUrl = url + "?page=";
        } else {
            pageUrl = url + "?" + queryString + "&page=";
            int end = pageUrl.indexOf("page=");
            pageUrl = pageUrl.substring(0, end + 5);
        }

        ApplicationContext appCtx = SpringContextUtils.getApplicationContext(context);
//        MatterMapper mapper=appCtx.getBean(MatterMapper.class);
//        /*
//         * 从标签读取“matterid”属性。标签中的这个可选属性将告诉我们需要什么样的数据
//         *
//         */
        int pageIndex = 1;
        int pageSize = 10;
        int totalSize = 0;
        int totalPage = 0;

        //当前页
        String pageIndexString = tag.getAttributeValue("value");  //tag.getAttributeValue("pageIndex");
        //页码大小
        String pageSizeString = tag.getAttributeValue("size");
        //数据大小
        String totalSizeString = tag.getAttributeValue("rows");

//        pageUrl = url + "?page=";

        if (!StringUtils.isEmpty(pageIndexString)) {
            try {
                pageIndex = Integer.parseInt(pageIndexString);
            } catch (Exception e) {
            }
        }
        if (!StringUtils.isEmpty(pageSizeString)) {
            try {
                pageSize = Integer.parseInt(pageSizeString);
            } catch (Exception e) {
            }
        }

        if (!StringUtils.isEmpty(totalSizeString)) {
            try {
                totalSize = Integer.parseInt(totalSizeString);
            } catch (Exception e) {
            }
        }

        if (totalSize == 0) structureHandler.replaceWith("", false);

        else {

            //计算总共有多少页
            totalPage = totalSize % pageSize == 0 ? totalSize / pageSize : totalSize / pageSize + 1;
            if (totalPage <= 1) structureHandler.replaceWith("", false);
            else {


                //是否还有下一页
                boolean hasNext = (pageIndex < totalPage);
                //是否还有上一页
                boolean hasPre = (pageIndex > 1);
                //是否是第一页
                boolean isFirst = (pageIndex == 1);
                //是否是最后一页
                boolean isEnd = (pageIndex == totalPage);


                /*
                 *  创建将替换自定义标签的DOM结构。
                 * logo将显示在“<div>”标签内, 因此必须首先创建,
                 * 然后必须向其中添加一个节点。
                 */
                final IModelFactory modelFactory = context.getModelFactory();

                final IModel model = modelFactory.createModel();


                /*model.add(modelFactory.createOpenElementTag("p"));*/
                if (isFirst) {
                    //如果是第一页
                    model.add(modelFactory.createText("<a href=\"javascript:;\" class=\"selected\">1</a>"));
                } else {
                    //首页
                    model.add(modelFactory.createOpenElementTag("a href=\"" + pageUrl + 1 + "\" class=\"prev\""));
                    model.add(modelFactory.createText("首页"));
                    model.add(modelFactory.createCloseElementTag("a"));
                }
                if (hasPre) {
                    //还有上一页，显示上一页按钮
                    model.add(modelFactory.createOpenElementTag("a href=\"" + pageUrl + (pageIndex - 1) + "\""));
                    model.add(modelFactory.createText("&lt;上一页"));
                    model.add(modelFactory.createCloseElementTag("a"));

                    //还有上一页，显示上一页页码
                    model.add(modelFactory.createOpenElementTag("a href=\"" + pageUrl + (pageIndex - 1) + "\""));
                    model.add(modelFactory.createText((pageIndex - 1) + ""));
                    model.add(modelFactory.createCloseElementTag("a"));

                }

                //中间页码，从当前页开始
                if (!isFirst) {
                    model.add(modelFactory.createOpenElementTag("a href=\"javascript:;\" class=\"selected\" "));
                    model.add(modelFactory.createText(pageIndex + ""));
                    model.add(modelFactory.createCloseElementTag("a"));
                }

                if (hasNext) {
                    //显示下一页的页码
                    model.add(modelFactory.createOpenElementTag("a href=\"" + pageUrl + (pageIndex + 1) + "\""));
                    model.add(modelFactory.createText((pageIndex + 1) + ""));
                    model.add(modelFactory.createCloseElementTag("a"));

                    //显示中间的...
                    model.add(modelFactory.createText("<span>...</span>"));

                    //显示尾页号码
                    /*model.add(modelFactory.createOpenElementTag("a href=\"" + pageUrl + totalPage + "\""));
                    model.add(modelFactory.createText(totalPage + ""));
                    model.add(modelFactory.createCloseElementTag("a"));*/

                    //显示下一页
                    model.add(modelFactory.createOpenElementTag("a href=\"" + pageUrl + (pageIndex + 1) + "\""));
                    model.add(modelFactory.createText("下一页&gt;"));
                    model.add(modelFactory.createCloseElementTag("a"));
                    //显示尾页
                    model.add(modelFactory.createOpenElementTag("a href=\"" + pageUrl + totalPage + "\" class=\"next\" "));
                    model.add(modelFactory.createText("尾页"));
                    model.add(modelFactory.createCloseElementTag("a"));
                }
                model.add(modelFactory.createOpenElementTag("span"));
                model.add(modelFactory.createText("总共"+totalPage+"页"));
                model.add(modelFactory.createCloseElementTag("a"));

                /*model.add(modelFactory.createCloseElementTag("p"));*/

                /*
                 * 指示引擎用指定的模型替换整个元素。
                 */
                structureHandler.replaceWith(model, false);
            }
        }
    }
}