package io.ei.jsontoxls.resources;

import io.ei.jsontoxls.repository.TemplateRepository;
import io.ei.jsontoxls.util.ExcelUtils;
import io.ei.jsontoxls.util.JsonPojoConverter;
import io.ei.jsontoxls.util.ObjectDeserializer;
import io.ei.jsontoxls.util.PackageUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class JsonToXlsResourceTest {

    private XlsResource xlsResource;

    @Mock
    private ObjectDeserializer objectDeserializer;
    @Mock
    private PackageUtils packageUtil;
    @Mock
    private JsonPojoConverter converter;
    @Mock
    private ExcelUtils excelUtil;
    @Mock
    private StreamingOutput streamingOutput;
    @Mock
    private TemplateRepository templateRepository;
    private String excelTemplate = "excel-template-name";

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        xlsResource = new XlsResource(converter, objectDeserializer, packageUtil, excelUtil,
                templateRepository);
    }

    @Test
    public void shouldGenerateXLSUsingTokenAndJSONData() throws Exception {
        String dataJson = "{\n" +
                "    \"loc\": {\n" +
                "        \"state\": \"Karnataka\",\n" +
                "        \"district\": \"Mysore\"\n" +
                "    },\n" +
                "    \"ind\": {\n" +
                "        \"anc\": \"1\",\n" +
                "        \"anc_12\": \"2\",\n" +
                "        \"anc_jsy\": \"3\"\n" +
                "    }\n" +
                "}";
        byte[] template = new byte[]{};
        when(templateRepository.findByToken("token")).thenReturn(template);
        when(converter.generateJavaClasses(dataJson)).thenReturn("generated-package-name");
        when(objectDeserializer.makeJsonObject("generated-package-name", dataJson)).thenReturn(new Object());
        when(excelUtil.generateExcelWorkbook(new HashMap<String, Object>(), template))
                .thenReturn(streamingOutput);

        Response response = xlsResource.generateExcelFromTemplate("token", dataJson);

        assertEquals(response.getStatus(), Response.ok().build().getStatus());
        verify(packageUtil).cleanup("generated-package-name");
    }

    @Test
    public void shouldReturnErrorWhenNoTemplateExistsForGivenToken() throws Exception {
        String dataJson = "{\n" +
                "    \"loc\": {\n" +
                "        \"state\": \"Karnataka\",\n" +
                "        \"district\": \"Mysore\"\n" +
                "    },\n" +
                "    \"ind\": {\n" +
                "        \"anc\": \"1\",\n" +
                "        \"anc_12\": \"2\",\n" +
                "        \"anc_jsy\": \"3\"\n" +
                "    }\n" +
                "}";
        when(templateRepository.findByToken("token")).thenReturn(null);

        Response response = xlsResource.generateExcelFromTemplate("token", dataJson);

        assertEquals(response.getStatus(), Response.status(404).entity("Could not find a valid template for the given token. Token: {0}").build().getStatus());
        verifyZeroInteractions(excelUtil);
        verifyZeroInteractions(converter);
        verifyZeroInteractions(objectDeserializer);
    }
}

