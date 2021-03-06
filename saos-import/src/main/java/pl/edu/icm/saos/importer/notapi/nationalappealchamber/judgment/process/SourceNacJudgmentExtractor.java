package pl.edu.icm.saos.importer.notapi.nationalappealchamber.judgment.process;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.edu.icm.saos.importer.common.JudgmentMeansOfAppealCreator;
import pl.edu.icm.saos.importer.common.JudgmentResultCreator;
import pl.edu.icm.saos.importer.common.converter.JudgmentDataExtractor;
import pl.edu.icm.saos.importer.common.correction.ImportCorrectionList;
import pl.edu.icm.saos.importer.notapi.common.SourceJudgeExtractorHelper;
import pl.edu.icm.saos.importer.notapi.nationalappealchamber.judgment.json.SourceNacJudgment;
import pl.edu.icm.saos.persistence.model.CourtCase;
import pl.edu.icm.saos.persistence.model.Judge;
import pl.edu.icm.saos.persistence.model.Judgment.JudgmentType;
import pl.edu.icm.saos.persistence.model.CourtType;
import pl.edu.icm.saos.persistence.model.JudgmentReferencedRegulation;
import pl.edu.icm.saos.persistence.model.JudgmentResult;
import pl.edu.icm.saos.persistence.model.JudgmentTextContent;
import pl.edu.icm.saos.persistence.model.MeansOfAppeal;
import pl.edu.icm.saos.persistence.model.NationalAppealChamberJudgment;
import pl.edu.icm.saos.persistence.model.SourceCode;

import com.google.common.collect.Lists;

/**
 * @author madryk
 */
@Service("sourceNacJudgmentExtractor")
public class SourceNacJudgmentExtractor implements JudgmentDataExtractor<NationalAppealChamberJudgment, SourceNacJudgment> {

    private JudgmentMeansOfAppealCreator judgmentMeansOfAppealCreator;
    
    private JudgmentResultCreator judgmentResultCreator;
    
    private SourceJudgeExtractorHelper sourceJudgeExtractorHelper;
    
    
    //------------------------ LOGIC --------------------------
    
    @Override
    public NationalAppealChamberJudgment createNewJudgment() {
        return new NationalAppealChamberJudgment();
    }

    @Override
    public List<CourtCase> extractCourtCases(SourceNacJudgment sourceJudgment, ImportCorrectionList correctionList) {
        List<CourtCase> courtCases = Lists.newArrayList();
        sourceJudgment.getCaseNumbers().forEach(x -> courtCases.add(new CourtCase(x)));
        
        return courtCases;
    }

    @Override
    public JudgmentTextContent extractTextContent(SourceNacJudgment sourceJudgment, ImportCorrectionList correctionList) {
        JudgmentTextContent textContent = new JudgmentTextContent();
        textContent.setRawTextContent(sourceJudgment.getTextContent());
        
        return textContent;
    }

    @Override
    public DateTime extractPublicationDate(SourceNacJudgment sourceJudgment, ImportCorrectionList correctionList) {
        return null;
    }

    @Override
    public String extractPublisher(SourceNacJudgment sourceJudgment, ImportCorrectionList correctionList) {
        return null;
    }

    @Override
    public String extractReviser(SourceNacJudgment sourceJudgment, ImportCorrectionList correctionList) {
        return null;
    }

    @Override
    public List<Judge> extractJudges(SourceNacJudgment sourceJudgment, ImportCorrectionList correctionList) {
        return sourceJudgeExtractorHelper.extractJudges(sourceJudgment, correctionList);
    }

    @Override
    public List<JudgmentReferencedRegulation> extractReferencedRegulations(
            SourceNacJudgment sourceJudgment, ImportCorrectionList correctionList) {
        return Lists.newArrayList();
    }

    @Override
    public JudgmentType extractJudgmentType(SourceNacJudgment sourceJudgment, ImportCorrectionList correctionList) {
        return JudgmentType.valueOf(sourceJudgment.getJudgmentType());
    }

    @Override
    public List<String> extractLegalBases(SourceNacJudgment sourceJudgment, ImportCorrectionList correctionList) {
        return Lists.newArrayList();
    }

    @Override
    public String extractSummary(SourceNacJudgment sourceJudgment, ImportCorrectionList correctionList) {
        return null;
    }

    @Override
    public String extractDecision(SourceNacJudgment sourceJudgment, ImportCorrectionList correctionList) {
        return null;
    }

    @Override
    public List<String> extractCourtReporters(SourceNacJudgment sourceJudgment, ImportCorrectionList correctionList) {
        return sourceJudgment.getCourtReporters();
    }

    @Override
    public LocalDate extractJudgmentDate(SourceNacJudgment sourceJudgment, ImportCorrectionList correctionList) {
        return sourceJudgment.getJudgmentDate();
    }
    
    @Override
    public LocalDate extractReceiptDate(SourceNacJudgment sourceJudgment, ImportCorrectionList correctionList) {
        return sourceJudgment.getReceiptDate();
    }

    @Override
    public List<String> extractLowerCourtJudgments(SourceNacJudgment sourceJudgment, ImportCorrectionList correctionList) {
        return Lists.newArrayList(sourceJudgment.getLowerCourtJudgments());
    }

    @Override
    public MeansOfAppeal extractMeansOfAppeal(SourceNacJudgment sourceJudgment, ImportCorrectionList correctionList) {
        if (StringUtils.isEmpty(sourceJudgment.getMeansOfAppeal())) {
            return null;
        }
        return judgmentMeansOfAppealCreator.fetchOrCreateMeansOfAppeal(CourtType.NATIONAL_APPEAL_CHAMBER, sourceJudgment.getMeansOfAppeal());
    }

    @Override
    public JudgmentResult extractJudgmentResult(SourceNacJudgment sourceJudgment, ImportCorrectionList correctionList) {
        if (StringUtils.isEmpty(sourceJudgment.getJudgmentResult())) {
            return null;
        }
        return judgmentResultCreator.fetchOrCreateJudgmentResult(CourtType.NATIONAL_APPEAL_CHAMBER, sourceJudgment.getJudgmentResult());
    }

    @Override
    public String extractSourceJudgmentId(SourceNacJudgment sourceJudgment, ImportCorrectionList correctionList) {
        return sourceJudgment.getSource().getSourceJudgmentId();
    }

    @Override
    public String extractSourceJudgmentUrl(SourceNacJudgment sourceJudgment, ImportCorrectionList correctionList) {
        return sourceJudgment.getSource().getSourceJudgmentUrl();
    }

    @Override
    public SourceCode getSourceCode() {
        return SourceCode.NATIONAL_APPEAL_CHAMBER;
    }

    @Override
    public void convertSpecific(NationalAppealChamberJudgment judgment,
            SourceNacJudgment sourceJudgment, ImportCorrectionList correctionList) {
        
    }

    
    //------------------------ SETTERS --------------------------

    @Autowired
    public void setJudgmentMeansOfAppealCreator(JudgmentMeansOfAppealCreator judgmentMeansOfAppealCreator) {
        this.judgmentMeansOfAppealCreator = judgmentMeansOfAppealCreator;
    }

    @Autowired
    public void setJudgmentResultCreator(JudgmentResultCreator judgmentResultCreator) {
        this.judgmentResultCreator = judgmentResultCreator;
    }

    @Autowired
    public void setSourceJudgeExtractorHelper(
            SourceJudgeExtractorHelper sourceJudgeExtractorHelper) {
        this.sourceJudgeExtractorHelper = sourceJudgeExtractorHelper;
    }

}
