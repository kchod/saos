package pl.edu.icm.saos.api.dump.court;

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

import pl.edu.icm.saos.api.dump.court.views.DumpCourtsView;
import pl.edu.icm.saos.api.search.parameters.Pagination;
import pl.edu.icm.saos.api.search.parameters.ParametersExtractor;
import pl.edu.icm.saos.api.services.exceptions.ControllersEntityExceptionHandler;
import pl.edu.icm.saos.api.services.exceptions.WrongRequestParameterException;
import pl.edu.icm.saos.api.services.interceptor.RestrictParamsNames;
import pl.edu.icm.saos.persistence.model.CommonCourt;
import pl.edu.icm.saos.persistence.search.DatabaseSearchService;
import pl.edu.icm.saos.persistence.search.dto.CommonCourtSearchFilter;
import pl.edu.icm.saos.persistence.search.result.SearchResult;

/**
 * Represents page of list of common courts in dump service
 * @author pavtel
 */
@Controller
@RequestMapping("/api/dump/commonCourts")
public class DumpCommonCourtsController extends ControllersEntityExceptionHandler {


    private ParametersExtractor parametersExtractor;

    private DatabaseSearchService databaseSearchService;

    private DumpCourtsListSuccessRepresentationBuilder dumpCourtsListSuccessRepresentationBuilder;



    //------------------------ LOGIC --------------------------
    
    @RequestMapping(value = "")
    @RestrictParamsNames
    @ResponseBody
    public ResponseEntity<Object> showCourts(
            @RequestHeader(value = "Accept", required = false) String acceptHeader,
            @RequestParam(value = PAGE_SIZE, required = false, defaultValue = Pagination.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(value = PAGE_NUMBER, required = false, defaultValue = "0") int pageNumber,
            HttpServletRequest request
    ) throws WrongRequestParameterException {
        
        checkRequestMethod(request, HttpMethod.GET);
        checkAcceptHeader(acceptHeader, MediaType.APPLICATION_JSON);
        
        
        Pagination pagination = parametersExtractor.extractAndValidatePagination(pageSize, pageNumber);



        CommonCourtSearchFilter searchFilter = CommonCourtSearchFilter.builder()
                .limit(pagination.getPageSize())
                .offset(pagination.getOffset())
                .filter();

        SearchResult<CommonCourt> searchResult = databaseSearchService.search(searchFilter);


        DumpCourtsView representation = dumpCourtsListSuccessRepresentationBuilder
                .build(searchResult, pagination, linkTo(DumpCommonCourtsController.class).toUriComponentsBuilder());

        HttpHeaders httpHeaders = new HttpHeaders();

        return new ResponseEntity<>(representation, httpHeaders, HttpStatus.OK);
    }


    //------------------------ SETTERS --------------------------

    @Autowired
    public void setParametersExtractor(ParametersExtractor parametersExtractor) {
        this.parametersExtractor = parametersExtractor;
    }

    @Autowired
    public void setDatabaseSearchService(DatabaseSearchService databaseSearchService) {
        this.databaseSearchService = databaseSearchService;
    }

    @Autowired
    public void setDumpCourtsListSuccessRepresentationBuilder(DumpCourtsListSuccessRepresentationBuilder dumpCourtsListSuccessRepresentationBuilder) {
        this.dumpCourtsListSuccessRepresentationBuilder = dumpCourtsListSuccessRepresentationBuilder;
    }
}
