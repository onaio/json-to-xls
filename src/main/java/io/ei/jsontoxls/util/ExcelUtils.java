package io.ei.jsontoxls.util;


import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import static java.text.MessageFormat.format;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Map;

public class ExcelUtils {

    public boolean isExcel(byte[] templateData) {
        try {
            WorkbookFactory.create(new ByteArrayInputStream(templateData));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public byte[] generateExcel(Map<String, Object> beans,
                                byte[] template, String uuid) throws InvalidFormatException, IOException {

        String filePath = format("/tmp/{0}.xls", uuid);
        try (OutputStream os = new FileOutputStream(filePath)) {
            Context context = new Context(beans);
            JxlsHelper jxlsHelper = JxlsHelper.getInstance();
            jxlsHelper.setUseFastFormulaProcessor(false);
            jxlsHelper.processTemplate(new ByteArrayInputStream(template), os, context);
        }

        return IOUtils.toByteArray(new BufferedInputStream(new FileInputStream(filePath)));
    }
}
