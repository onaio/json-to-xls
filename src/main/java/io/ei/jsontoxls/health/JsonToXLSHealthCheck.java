package io.ei.jsontoxls.health;

import com.yammer.metrics.core.HealthCheck;
import io.ei.jsontoxls.resources.XlsResource;

import javax.ws.rs.core.Response;

public class JsonToXLSHealthCheck extends HealthCheck {
    private XlsResource resource;

    public JsonToXLSHealthCheck(XlsResource resource) {
        super("JSON to XLS conversion");
        this.resource = resource;
    }

    @Override
    protected Result check() throws Exception {
        String dataJson = "{\"loc\":{\"state\":\"Karnataka\",\"district\":\"Mysore\",\"taluk\":\"T Narasipura\",\"phc\":\"Bherya\",\"sub_center\":\"Bherya-A\"},\"ind\":{\"anc\":\"1\",\"anc_12\":\"2\",\"anc_jsy\":\"3\",\"three_anc_visit\":\"4\",\"tt1\":\"5\",\"tt2B\":\"6\",\"ifa\":\"7\",\"hyp\":\"8\",\"hb11\":\"9\",\"home_sba\":\"10\",\"home_no_sba\":\"11\",\"home_24hrs\":\"12\",\"home_jsy\":\"13\",\"sc\":\"14\",\"sc_48\":\"15\",\"sc_jsy_mother\":\"16\",\"sc_jsy_asha\":\"17\",\"lb_male\":\"18\",\"lb_female\":\"19\",\"sb\":\"20\",\"abortion\":\"21\",\"weighed\":\"22\",\"lbw\":\"23\",\"bf_1hr\":\"24\",\"pnc_48hrs\":\"25\",\"pnc_14d\":\"26\",\"iud_insert\":\"27\",\"iud_remove\":\"28\",\"ocp\":\"29\",\"condom\":\"30\",\"centchroman\":\"31\",\"ecp\":\"32\",\"ms_comp\":\"33\",\"fs_comp\":\"34\",\"ms_fail\":\"35\",\"fs_fail\":\"36\",\"ms_death\":\"37\",\"fs_death\":\"38\",\"bcg\":\"39\",\"dpt1\":\"40\",\"dpt2\":\"41\",\"dpt3\":\"42\",\"opv0\":\"43\",\"opv1\":\"44\",\"opv2\":\"45\",\"opv3\":\"46\",\"hepb0\":\"47\",\"hepb1\":\"48\",\"hepb2\":\"49\",\"hepb3\":\"50\",\"measles\":\"51\",\"measlesb\":\"52\",\"immun_m_9to11\":\"53\",\"immun_f_9to11\":\"54\",\"dptb1\":\"55\",\"opvb\":\"56\",\"mmr\":\"57\",\"immun_m_12to23\":\"58\",\"immun_f_12to23\":\"59\",\"dptb2\":\"60\",\"aefi_abscess\":\"61\",\"aefi_death\":\"62\",\"aefi_others\":\"63\",\"vita1\":\"64\",\"vita5\":\"65\",\"vita9\":\"66\",\"rep_measles\":\"67\",\"rep_diarrhea\":\"68\",\"rep_malaria\":\"69\"}}";
        Response response = resource.generateExcelFromTemplate(dataJson);

        if (response.getStatus() == 200) {
            return Result.healthy();
        }
        return Result.unhealthy("Got a response that was different from 200.");
    }
}
