package io.ei.jsontoxls.util;

import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
                                byte[] template) throws InvalidFormatException, IOException {
        XLSTransformer transformer = new XLSTransformer();
        Workbook workbook = transformer.transformXLS(new ByteArrayInputStream(template), beans);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return outputStream.toByteArray();
    }
}