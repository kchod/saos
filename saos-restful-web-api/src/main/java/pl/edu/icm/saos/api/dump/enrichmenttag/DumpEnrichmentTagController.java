package pl.edu.icm.saos.api.dump.enrichmenttag;

import static pl.edu.icm.saos.api.ApiConstants.PAGE_NUMBER;
import static pl.edu.icm.saos.api.ApiConstants.PAGE_SIZE;
import static pl.edu.icm.saos.api.services.exceptions.HttpServletRequestVerifyUtils.checkAcceptHeader;
import static pl.edu.icm.saos.api.services.exceptions.HttpServletRequestVerifyUtils.checkRequestMethod;
import static pl.edu.icm.saos.api.services.links.ControllerProxyLinkBuilder.linkTo;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pl.edu.icm.saos.api.dump.enrichmenttag.views.DumpEnrichmentTagsView;
import pl.edu.icm.saos.api.search.parameters.Pagination;
import pl.edu.icm.saos.api.search.parameters.ParametersExtractor;
import pl.edu.icm.saos.api.services.exceptions.ControllersEntityExceptionHandler;
import pl.edu.icm.saos.api.services.exceptions.WrongRequestParameterException;
import pl.edu.icm.saos.api.services.interceptor.RestrictParamsNames;
import pl.edu.icm.saos.persistence.enrichment.model.EnrichmentTag;
import pl.edu.icm.saos.persistence.search.DatabaseSearchService;
import pl.edu.icm.saos.persistence.search.dto.EnrichmentTagSearchFilter;
import pl.edu.icm.saos.persistence.search.result.SearchResult;

/**
 * @author madryk
 */
@Controller
@RequestMapping("/api/dump/enrichments")
public class DumpEnrichmentTagController extends ControllersEntityExceptionHandler {

    private ParametersExtractor parametersExtractor;

    private DatabaseSearchService databaseSearchService;
    
    private DumpEnrichmentTagsListSuccessRepresentationBuilder dumpEnrichmentTagsListSuccessRepresentationBuilder;
    
    
    //------------------------ LOGIC --------------------------
    
    @RequestMapping(value = "")
    @RestrictParamsNames
    @ResponseBody
    public ResponseEntity<Object> showEnrichmentTags(
            @RequestHeader(value = "Accept", required = false) String acceptHeader,
            @RequestParam(value = PAGE_SIZE, required = false, defaultValue = Pagination.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(value = PAGE_NUMBER, required = false, defaultValue = "0") int pageNumber,
            HttpServletRequest request
    ) throws WrongRequestParameterException {

        checkRequestMethod(request, HttpMethod.GET);
        checkAcceptHeader(acceptHeader, MediaType.APPLICATION_JSON);

        Pagination pagination = parametersExtractor.extractAndValidatePagination(pageSize, pageNumber);



        EnrichmentTagSearchFilter searchFilter = EnrichmentTagSearchFilter.builder()
                .limit(pagination.getPageSize())
                .offset(pagination.getOffset())
                .filter();

        SearchResult<EnrichmentTag> searchResult = databaseSearchService.search(searchFilter);


        DumpEnrichmentTagsView representation = dumpEnrichmentTagsListSuccessRepresentationBuilder
                .build(searchResult, pagination, linkTo(DumpEnrichmentTagController.class).toUriComponentsBuilder());

        HttpHeaders httpHeaders = new HttpHeaders();

        return new ResponseEntity<>(representation, httpHeaders, HttpStatus.OK);
    }

    
    //------------------------ LOGIC --------------------------

    @Autowired
    public void setParametersExtractor(ParametersExtractor parametersExtractor) {
        this.parametersExtractor = parametersExtractor;
    }

    @Autowired
    public void setDatabaseSearchService(DatabaseSearchService databaseSearchService) {
        this.databaseSearchService = databaseSearchService;
    }

    @Autowired
    public void setDumpEnrichmentTagsListSuccessRepresentationBuilder(
            DumpEnrichmentTagsListSuccessRepresentationBuilder dumpEnrichmentTagsListSuccessRepresentationBuilder) {
        this.dumpEnrichmentTagsListSuccessRepresentationBuilder = dumpEnrichmentTagsListSuccessRepresentationBuilder;
    }
    
}
