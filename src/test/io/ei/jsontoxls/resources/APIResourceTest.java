package io.ei.jsontoxls.resources;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class APIResourceTest {

    @Test
    public void shouldReturnTheAPIDetails() throws Exception {
        String expectedDetails = "POST /templates, with excel template file as payload.\n" +
                "    This saves the template and returns a unique token for that template in the response. Henceforth this token can be used for referring this template.\n" +
                "POST /xls/<template_token> with JSON data as payload.\n" +
                "    This will use the template corresponding to the token in the URL and generate an Excel file using the template and JSON data. The URL using which this excel file can be downloaded is returned in the response.\n" +
                "GET /xls/<generated_excel_token>\n" +
                "    This returns the excel file for given token.";

        assertEquals(expectedDetails, new APIResource("api.txt").details().getEntity());
    }
}
