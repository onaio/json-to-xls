package io.ei.jsontoxls.resources;

import com.google.gson.Gson;
import io.ei.jsontoxls.core.Data;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Path("/xls")
@Produces({"application/ms-excel"})
public class JsonToXlsResource {
    Logger logger = LoggerFactory.getLogger(JsonToXlsResource.class);
    private final String excelTemplate;

    public JsonToXlsResource(String excelTemplate) {
        this.excelTemplate = excelTemplate;
    }

    @GET
    public Response generateExcelFromTemplate() {
        String dataJson = "{\"loc\":{\"state\":\"Karnataka\",\"district\":\"Mysore\",\"taluk\":\"T Narasipura\",\"phc\":\"Bherya\",\"sub_center\":\"Bherya-A\"},\"ind\":{\"anc\":\"1\",\"anc_12\":\"2\",\"anc_jsy\":\"3\",\"three_anc_visit\":\"4\",\"tt1\":\"5\",\"tt2B\":\"6\",\"ifa\":\"7\",\"hyp\":\"8\",\"hb11\":\"9\",\"home_sba\":\"10\",\"home_no_sba\":\"11\",\"home_24hrs\":\"12\",\"home_jsy\":\"13\",\"sc\":\"14\",\"sc_48\":\"15\",\"sc_jsy_mother\":\"16\",\"sc_jsy_asha\":\"17\",\"lb_male\":\"18\",\"lb_female\":\"19\",\"sb\":\"20\",\"abortion\":\"21\",\"weighed\":\"22\",\"lbw\":\"23\",\"bf_1hr\":\"24\",\"pnc_48hrs\":\"25\",\"pnc_14d\":\"26\",\"iud_insert\":\"27\",\"iud_remove\":\"28\",\"ocp\":\"29\",\"condom\":\"30\",\"centchroman\":\"31\",\"ecp\":\"32\",\"ms_comp\":\"33\",\"fs_comp\":\"34\",\"ms_fail\":\"35\",\"fs_fail\":\"36\",\"ms_death\":\"37\",\"fs_death\":\"38\",\"bcg\":\"39\",\"dpt1\":\"40\",\"dpt2\":\"41\",\"dpt3\":\"42\",\"opv0\":\"43\",\"opv1\":\"44\",\"opv2\":\"45\",\"opv3\":\"46\",\"hepb0\":\"47\",\"hepb1\":\"48\",\"hepb2\":\"49\",\"hepb3\":\"50\",\"measles\":\"51\",\"measlesb\":\"52\",\"immun_m_9to11\":\"53\",\"immun_f_9to11\":\"54\",\"dptb1\":\"55\",\"opvb\":\"56\",\"mmr\":\"57\",\"immun_m_12to23\":\"58\",\"immun_f_12to23\":\"59\",\"dptb2\":\"60\",\"aefi_abscess\":\"61\",\"aefi_death\":\"62\",\"aefi_others\":\"63\",\"vita1\":\"64\",\"vita5\":\"65\",\"vita9\":\"66\",\"rep_measles\":\"67\",\"rep_diarrhea\":\"68\",\"rep_malaria\":\"69\"}}";

        Map beans = new HashMap();
        beans.put("data", new Gson().fromJson(dataJson, Data.class));
        XLSTransformer transformer = new XLSTransformer();
        try {
            FileInputStream inputStream = new FileInputStream("NRHM-Monthly-Report-Format-Sub-Centers.xls");

            Workbook workbook = transformer.transformXLS(inputStream, beans);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return Response.ok(getOut(outputStream.toByteArray())).build();
        } catch (Exception e) {
            logger.error("XLS Transformation failed. Exception: " + e);
            return Response.status(503).build();
        }
    }

    private StreamingOutput getOut(final byte[] excelBytes) {
        return new StreamingOutput() {
            @Override
            public void write(OutputStream out) throws IOException, WebApplicationException {
                out.write(excelBytes);
            }
        };
    }
}
