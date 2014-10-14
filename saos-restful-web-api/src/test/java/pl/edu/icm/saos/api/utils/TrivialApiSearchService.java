package pl.edu.icm.saos.api.utils;

import pl.edu.icm.saos.api.judgments.parameters.JudgmentsParameters;
import pl.edu.icm.saos.api.search.ApiSearchService;
import pl.edu.icm.saos.api.search.ElementsSearchResults;
import pl.edu.icm.saos.persistence.model.CommonCourtJudgment;
import pl.edu.icm.saos.persistence.model.Judgment;

import java.util.Arrays;

import static pl.edu.icm.saos.api.utils.FieldsDefinition.createCommonJudgment;

/**
 * @author pavtel
 */
public class TrivialApiSearchService implements ApiSearchService<Judgment, JudgmentsParameters>{

    private int totalResults;

    public TrivialApiSearchService(int totalResults) {
        this.totalResults = totalResults;
    }

    @Override
    public ElementsSearchResults<Judgment, JudgmentsParameters> performSearch(JudgmentsParameters parameters) {
        CommonCourtJudgment commonCourtJudgment = createCommonJudgment();
        return new ElementsSearchResults<Judgment, JudgmentsParameters>(Arrays.asList(commonCourtJudgment), parameters)
                .totalResults(totalResults);
    }
}
