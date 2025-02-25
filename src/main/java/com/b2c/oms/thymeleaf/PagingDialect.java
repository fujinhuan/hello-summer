package com.b2c.oms.thymeleaf;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.HashSet;
import java.util.Set;

public class PagingDialect extends AbstractProcessorDialect {
    private static final String DIALECT_NAME = "PagingDialect";//定义方言名称

    public PagingDialect() {
        super(DIALECT_NAME, "paging", StandardDialect.PROCESSOR_PRECEDENCE);
        //StandardDialect.PROCESSOR_PRECEDENCE
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        Set<IProcessor> processors = new HashSet<IProcessor>();
        processors.add(new PagingTagProcessor(dialectPrefix));//添加我们定义的标签


        processors.add(new StandardXmlNsTagProcessor(TemplateMode.HTML, dialectPrefix));
        return processors;

    }
}
