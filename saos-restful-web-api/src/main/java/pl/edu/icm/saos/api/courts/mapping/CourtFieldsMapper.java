package pl.edu.icm.saos.api.courts.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.icm.saos.api.links.LinksBuilder;
import pl.edu.icm.saos.api.mapping.FieldsMapper;
import pl.edu.icm.saos.persistence.model.CommonCourt;

import java.util.LinkedHashMap;
import java.util.Map;

import static pl.edu.icm.saos.api.ApiConstants.*;

/**
 * @author pavtel
 */
@Component("commonCourtFieldsMapper")
public class CourtFieldsMapper implements FieldsMapper<CommonCourt> {

    @Autowired
    private LinksBuilder linksBuilder;


    @Override
    public Map<String, Object> basicFieldsToMap(CommonCourt court) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put(HREF, linksBuilder.urlToCourt(court.getId()));
        item.put(NAME, court.getName());

        return item;
    }

    @Override
    public Map<String, Object> fieldsToMap(CommonCourt court) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.putAll(basicFieldsToMap(court));

        item.put(CODE, court.getCode());
        item.put(TYPE, court.getType());

        if(court.getParentCourt() != null){
            item.put(PARENT_COURT, basicFieldsToMap(court.getParentCourt()));
        }

        return item;
    }


    //*** setters ***
    public LinksBuilder getLinksBuilder() {
        return linksBuilder;
    }
}
