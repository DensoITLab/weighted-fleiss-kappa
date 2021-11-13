///////////////////////////////////////////////////////////////////////////////////////////////////////
/// Inter-Annotator Agreement
/// Copyright (c) 2021 DENSO IT LABORATORY, INC. All rights reserved.
///
/// Unless required by applicable law or agreed to in writing, 
/// software distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
///////////////////////////////////////////////////////////////////////////////////////////////////////

/// History:
/// [000] 2021/10/14, Hiroshi Tsukahara, Created.
///
package jp.co.d_itlab.dbdc.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import jp.co.d_itlab.dbdc.excel.BreakDownAnnotatedUtterance;
import jp.co.d_itlab.dbdc.excel.ErrorCategoryAnnotatedUtterance;
import jp.co.d_itlab.dbdc.excel.Field;
import jp.co.d_itlab.dbdc.excel.FieldName;
import jp.co.d_itlab.dbdc.excel.Utterance;
import jp.co.d_itlab.dbdc.logging.DateTimeFormat;
import jp.co.d_itlab.dbdc.logging.DoubleFormat;
import jp.co.d_itlab.dbdc.logging.FootPrint;
import jp.co.d_itlab.dbdc.model.AnnotationDataset;
import jp.co.d_itlab.dbdc.model.BreakdownClassifier;
import jp.co.d_itlab.dbdc.model.Dialogue;
import jp.co.d_itlab.dbdc.model.DialogueDataset;
import jp.co.d_itlab.dbdc.model.ErrorCategory;
import jp.co.d_itlab.iaa.AnnotationMatrix;
import jp.co.d_itlab.iaa.ConfusionMatrix;
import jp.co.d_itlab.iaa.WeightedFleissKappa;
import jp.co.d_itlab.iaa.WeightedKappa;
import jp.co.d_itlab.math.IndexedMatrix;
import jp.co.d_itlab.math.VarianceEstimator;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;


/**
 * Tools to analyze data annotated with dialogue breakdown error categories.
 * Error categories are assumed to be multi-labeled.
 * 
 * @param <K> - data index type
 * @param <L> - label type
 *
 */
@Command(name="iaa", mixinStandardHelpOptions = true, version = "V1.0", description = "Innter Annotator Agreement Analysis Tool")
public class IAATool  extends CliTool
{
    public final static String OPT_ERROR_CATEGORY = "-c";
    @Option(names = {OPT_ERROR_CATEGORY}, required = false, description="error category")
    private static String targetErrorCategory = "TD";
    
    public final static String OPT_INPUT_PATH = "-i";
    @Option(names = {OPT_INPUT_PATH}, required = false, description="input path")
    private static String inputPath = "./";
    
    public final static String OPT_OUTPUT_PATH = "-o";
    @Option(names = {OPT_OUTPUT_PATH}, required = false, description="output path")
    private static String outputPath;
    
    public final static String OPT_ANNOTATOR_1 = "-a1";
    @Option(names = {OPT_ANNOTATOR_1}, required = false, description="annotator 1")
    private static String annotator1;
    
    public final static String OPT_ANNOTATOR_2 = "-a2";
    @Option(names = {OPT_ANNOTATOR_2}, required = false, description="annotator 2")
    private static String annotator2;
    
    public final static String OPT_TARGET_ANNOTATORS = "-a";
    @Option(names = {OPT_TARGET_ANNOTATORS}, required = false, split=",",  description="array of annotators")
    private static String[] targetAnnotatorArray;
    
    public final static String OPT_FILTER_DIALOGUE_SYSTEM = "-ds";
    @Option(names = {OPT_FILTER_DIALOGUE_SYSTEM}, required = false, description="dislogue system ID")
    private static String filterDialogueSystemId;
    
    public final static String OPT_DIC_PATH = "-dic";
    @Option(names = {OPT_DIC_PATH}, required = false, description="path to dictionaries", defaultValue="./res/dic")
    private static String dicPath;
    
    public final static String OPT_LANGUAGE = "-l";
    @Option(names = {OPT_LANGUAGE}, required = false, description="language (ja: Japanese, en: English)", defaultValue="ja")
    private static String locale;
    
    private NumberFormat nf = NumberFormat.getInstance();
    
    // Annotation data
    private List<String> targetAnnotatorList;
    private List<String> annotators;
    private AnnotationDataset<String> annotationDataset;
    
    // dialogue data
    private DialogueDataset dialogueDataset;
    
    // dialogue data for each annotator
    private Map<String, DialogueDataset> annotatorDialogueDataset;
    
    public static String ERROR_CATEGORY_TD_NAME = "TD";
    public static String ERROR_CATEGORY_BU_NAME = "BU";
    public static String ERROR_CATEGORY_INT_NAME = "INT";
    
    private String[] ERROR_CATEGORY_NAMES = new String[] {
            ERROR_CATEGORY_TD_NAME,
            ERROR_CATEGORY_BU_NAME,
            ERROR_CATEGORY_INT_NAME
    };
    
    private Map<String, ErrorCategory<String>> errorCategoryMap;
    
    private Map<String, Map<String, List<Integer>>> dialogueSessionAndTurnId = new HashMap<>();
    
    public IAATool()
    {
        setCommand(InterAnnotatorAgreementComand.NAME, new InterAnnotatorAgreementComand());
        setCommand(AllInterAnnotatorAgreementsComand.NAME, new AllInterAnnotatorAgreementsComand());
        setCommand(MultiAnnotatorAgreementCommand.NAME, new MultiAnnotatorAgreementCommand());
    }

    @Override
    public void preprocess()
    {
        super.preprocess();
        
        // prepare output directory
        if (outputPath != null)
        {
            preparePath(outputPath);
        }
        
        // load error category definition
        if (errorCategoryMap == null)
        {
            FootPrint.show("Loading error category dictionaries...");
            errorCategoryMap = new HashMap<>();
            String templateDicPath = dicPath;
            FootPrint.debug("user.dir = " + System.getProperty("user.dir"));
            if ("ja".equals(locale))
            {
                templateDicPath += "/%s_ja.dic";
            }
            else
            {
                templateDicPath += "/%s.dic";
            }
            for (String ecName : ERROR_CATEGORY_NAMES)
            {
                errorCategoryMap.put(ecName, new ErrorCategory(ecName, String.format(templateDicPath, ecName)));
            }
        }
    }

    @Override
    public void postprocess()
    {
        super.postprocess();
    }
    
    private void loadData(String path)
    {
        FootPrint.show("Start loading data...");
        
        // initialize data
        annotationDataset = new AnnotationDataset<>(getCategory());
        dialogueDataset = new DialogueDataset();
        annotators = new ArrayList<>(); 
        if (targetAnnotatorArray != null)
        {
            targetAnnotatorList = Arrays.asList(targetAnnotatorArray);
        }
        else
        {
            targetAnnotatorList = Arrays.asList(annotator1, annotator2);
        }
                
        // load data for each annotator
        for (File annotator : getDirectories(path))
        {
            if (!annotator.getName().equals(annotator1) && !annotator.getName().equals(annotator2) && !targetAnnotatorList.contains(annotator.getName()))
            {
                continue;
            }
            
            // load data for each category type
            FootPrint.info("Annotator: " + annotator.getName());
            FootPrint.info("Category Type: " + targetErrorCategory);
            if (!annotators.contains(annotator.getName()))
            {
                annotators.add(annotator.getName());
            }
            
            // load data for each group
            for (File trial : getDirectories(annotator.getAbsolutePath()))
            {
                FootPrint.show("Trial: " + trial.getName());
                // load annotated files
                for (File f : getFiles(trial.getAbsolutePath()))
                {
                    List<ErrorCategoryAnnotatedUtterance<String>> utterances = loadFile(annotator.getName(), targetErrorCategory, f);
                }
            }
        }
        
        Map<String, Integer> numAnnotations = annotationDataset.getNumAnnotations();
        for (String aid : numAnnotations.keySet())
        {
            FootPrint.info("Annotator: [{0}],  Num Annotations: [{1}]", aid, numAnnotations.get(aid));
        }
    }
    
    private <T> List<ErrorCategoryAnnotatedUtterance<T>> loadFile(String annotator, String categoryType, File f)
    {
        try
        {
            FootPrint.show("Loading a File: " + f.getAbsolutePath());
            Workbook wb = WorkbookFactory.create(f);
            return loadExcelData(wb, annotator, categoryType, f.getName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    private <T> List<ErrorCategoryAnnotatedUtterance<T>> loadExcelData(Workbook wb, String annotator, String categoryType, String fileName)
    {
        List<ErrorCategoryAnnotatedUtterance<T>> utterances = new ArrayList<>();
        
        Sheet sheet = wb.getSheetAt(0);
        Iterator<Row> rows = sheet.rowIterator();
        int n = 0;
        Dialogue currentDialogue = new Dialogue(null);
        while(rows.hasNext()) 
        {
            Row row = rows.next();
            
            if (n++ == 0)
            {
                // skip header
                continue;
            }
            
            if (row.getCell(0) == null)
            {
                // end of data
                break;
            }
            
            ErrorCategoryAnnotatedUtterance<String> u = new ErrorCategoryAnnotatedUtterance<>();
            u.categoryType = categoryType;
            u.setValue(FieldName.Annotator, annotator);
            u.setValue(FieldName.FileName, fileName);
            u.setValue(FieldName.DialogueId, getStirngValue(row.getCell(0)).replaceAll(",", ""));
            u.setValue(FieldName.GroupId, getStirngValue(row.getCell(1)).replaceAll(",", ""));
            u.setValue(FieldName.SpeakerId, getStirngValue(row.getCell(2)));
            u.setValue(FieldName.Speaker, getStirngValue(row.getCell(3)));
            u.setValue(FieldName.Time, getDateValue(row.getCell(4)));
            u.setValue(FieldName.TurnIndex, getStirngValue(row.getCell(5)).replaceAll(",", ""));
            u.setValue(FieldName.Utterance, getStirngValue(row.getCell(6)));
            u.setValue(FieldName.NumAnnotation, getStirngValue(row.getCell(7)));
            u.setValue(FieldName.NumO, getStirngValue(row.getCell(8)));
            u.setValue(FieldName.NumT, getStirngValue(row.getCell(9)));
            u.setValue(FieldName.NumX, getStirngValue(row.getCell(10)));
            u.setValue(FieldName.BreakdownCategory, getStirngValue(row.getCell(11)));
            u.setValue(FieldName.Remark, getStirngValue(row.getCell(12)));
            
            boolean isAdded = false;
            String sid = systemId(fileName);  
            String dialogueId = bareId(u.getDialogueId());
            
            if (filterDialogueSystemId != null)
            {
                if (filterDialogueSystemId.equals(sid))
                {
                    annotationDataset.add(u);
                    isAdded = true;
                }
            }
            else
            {
                annotationDataset.add(u);
                isAdded = true;
            }
            
            if (isAdded)
            {
                if (!dialogueSessionAndTurnId.containsKey(sid))
                {
                    dialogueSessionAndTurnId.put(sid, new HashMap<>());
                }
            
                Map<String, List<Integer>> dialogueIds =dialogueSessionAndTurnId.get(sid);
                if (!dialogueIds.containsKey(dialogueId))
                {
                    dialogueIds.put(dialogueId, new ArrayList<>());
                }
                dialogueIds.get(dialogueId).add(u.getTurnIndex());
            }

            if (!currentDialogue.isEqual(u.getDialogueId()))
            {
                currentDialogue = new Dialogue("" + u.getDialogueId());
                dialogueDataset.add(currentDialogue);
            }
            currentDialogue.add(u);
        }
        
        FootPrint.show(n + " rows read.");
        
        return utterances;
    }
    
    private String getStirngValue(Cell cell)
    {
        if (cell != null)
        {  
            switch(cell.getCellType())
            {
                //case Cell.CELL_TYPE_STRING:
                case STRING:
                    return cell.getStringCellValue();
                //case Cell.CELL_TYPE_NUMERIC:
                case NUMERIC:
                    return nf.format(cell.getNumericCellValue());
            }
        }
        
        return "";
    }
    
    private Date getDateValue(Cell cell)
    {
        if (cell != null)
        {  
            switch(cell.getCellType())
            {
                case STRING:
                // case Cell.CELL_TYPE_STRING:
                    if (cell.getStringCellValue().contains("/"))
                    {
                        
                        return DateUtil.parseYYYYMMDDDate(cell.getStringCellValue());
                    }
                    else
                    {
                        return DateTimeFormat.parseDDMMYYYYHHMMSS(cell.getStringCellValue());
                    }
                //case Cell.CELL_TYPE_NUMERIC:
                case NUMERIC:
                    return cell.getDateCellValue();
            }
        }
        
        return null;
    }

    private void mergeData(String filename)
    {
        Workbook wb = null;
        try
        {
            wb = new SXSSFWorkbook();
            Sheet sheet = wb.createSheet("Merged");
            
            CreationHelper createHelper = wb.getCreationHelper();
            CellStyle cellStyle = wb.createCellStyle();
            short style = createHelper.createDataFormat().getFormat("yyyy/mm/dd h:mm");
            cellStyle.setDataFormat(style);
            
            // write headers
            int nRow = 0;
            Row row = (row = sheet.getRow(nRow)) == null ? sheet.createRow(nRow) : row;
            int nCol = 0;
            Cell cell;
            cell = (cell = row.getCell(nCol)) == null ? row.createCell(nCol) : cell;
            for (String fieldName : BreakDownAnnotatedUtterance.getFieldNames())
            {   
                ++nCol;
                cell = (cell = row.getCell(nCol)) == null ? row.createCell(nCol) : cell;
                //cell.setCellType(Cell.CELL_TYPE_STRING);
                cell.setCellValue(fieldName);
            }
            for (String aid : annotators)
            {
                for (String fieldName : ErrorCategoryAnnotatedUtterance.getErrorCategoryAnnotationFieldNames())
                {  
                    ++nCol;
                    cell = (cell = row.getCell(nCol)) == null ? row.createCell(nCol) : cell;
                    //cell.setCellType(Cell.CELL_TYPE_STRING);
                    cell.setCellValue(fieldName);  
                }
            }
            
            ++nCol;
            cell = (cell = row.getCell(nCol)) == null ? row.createCell(nCol) : cell;
            //cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(FieldName.BreakdownCategory.getName());  
            ++nCol;
            cell = (cell = row.getCell(nCol)) == null ? row.createCell(nCol) : cell;
            //cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(FieldName.Remark.getName()); 
            
            // write body
            for (String did : dialogueDataset.getDialogueIds())
            {
                Dialogue d = dialogueDataset.getDialogue(did);
                
                for (Utterance u : d.getUtterances())
                {
                    BreakDownAnnotatedTurn dbTurn = new BreakDownAnnotatedTurn(u);
                    
                    ++nRow;
                    row = (row = sheet.getRow(nRow)) == null ? sheet.createRow(nRow) : row;
                    BreakDownAnnotatedUtterance bdau = (BreakDownAnnotatedUtterance)u;
                    dbTurn.numAnnotations = bdau.getNumAnnotation();
                    dbTurn.numOs = (int)bdau.getNumO();
                    dbTurn.numTs = (int)bdau.getNumT();
                    dbTurn.numXs = (int)bdau.getNumX();
                    nCol = 0;
                    cell = (cell = row.getCell(nCol)) == null ? row.createCell(nCol) : cell;
                    //cell.setCellType(Cell.CELL_TYPE_STRING);
                    cell.setCellValue((String)u.getValue(FieldName.FileName));
                    for (String fieldName : BreakDownAnnotatedUtterance.getFieldNames())
                    {
                        ++nCol;
                        cell = (cell = row.getCell(nCol)) == null ? row.createCell(nCol) : cell;
                        Field<?> field = bdau.getField(fieldName);
                        //cell.setCellType(field.gettype().getEcelCellType());
                        switch (field.gettype())
                        {
                            case String:
                                cell.setCellValue((String)bdau.getValue(fieldName));
                                break;
                            case Integer:
                                cell.setCellValue((Integer)bdau.getValue(fieldName));
                                break;
                            case Double:
                                cell.setCellValue((Double)bdau.getValue(fieldName));
                                break;
                            case Date:
                                cell.setCellValue((Date)bdau.getValue(fieldName));
                                cell.setCellStyle(cellStyle);
                                break;
                            default:
                                cell.setCellValue((String)bdau.getValue(fieldName));
                                break;
                        }
                        
                    }

                    boolean isBreakdown = false;
                    HashMap<String, Integer> bdLabels = new HashMap<>();
                    for (String aid : annotators)
                    {
                        Map<String, ErrorCategoryAnnotatedUtterance<String>> utterances =  annotationDataset.getAnnotations(aid);
                        ErrorCategoryAnnotatedUtterance<String> ecau = utterances.get(bdau.getId());
                        if (ecau != null)
                        {
                            isBreakdown = true;
                            for (String fieldName : ErrorCategoryAnnotatedUtterance.getErrorCategoryAnnotationFieldNames())
                            {
                                ++nCol;
                                cell = (cell = row.getCell(nCol)) == null ? row.createCell(nCol) : cell;
                                Field<?> field = ecau.getField(fieldName);
                                //cell.setCellType(field.gettype().getEcelCellType());
                                switch (field.gettype())
                                {
                                    case String:
                                        cell.setCellValue((String)ecau.getValue(fieldName));
                                        break;
                                    case Integer:
                                        cell.setCellValue((Integer)ecau.getValue(fieldName));
                                        break;
                                    case Double:
                                        cell.setCellValue((Double)ecau.getValue(fieldName));
                                        break;
                                    default:
                                        cell.setCellValue((String)ecau.getValue(fieldName));
                                        break;
                                }
                            }
                        }
                    }
                }
            }
            
            if (outputPath != null)
            {
                Path savePath = Paths.get(outputPath, filename + ".xlsx");
                FileOutputStream out = new FileOutputStream(new File(savePath.toAbsolutePath().toString()));
                wb.write(out);
                out.close();
                wb.close();
                FootPrint.info("Merged data output to {0}. (File: {1})", outputPath, savePath.toAbsolutePath().toString());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            if (wb != null)
            {
                try
                {
                    wb.close();
                }
                catch (Exception e2)
                {
                    e2.printStackTrace();
                }
            }
        }
    }
    
    private <L> ErrorCategory<L> getCategory()
    {
        if (errorCategoryMap.containsKey(targetErrorCategory))
        {
            return (ErrorCategory<L>)errorCategoryMap.get(targetErrorCategory);
        }
        else
        {
            throw new RuntimeException("Invalid target error category name: " + targetErrorCategory);
        }
    }
   
    private void printFreq(WeightedKappa<String, String> wk, String annotator)
    {
        List<String> category = wk.getCategory();
        Map<String, Double> freq = wk.getFreq(annotator);
        StringBuilder sb = new StringBuilder();
        for (String c : category)
        {
            sb.append(MessageFormat.format("{0}, {1}\n", c, freq.get(c)));
        }
        FootPrint.info("Frequency of category for " + annotator + "\n" + sb.toString());
    }
    
    private void printFreq(WeightedFleissKappa<String, String> wk, String annotator)
    {
        List<String> category = wk.getCategory();
        Map<String, Double> wfreq = wk.getWeightedFreq(annotator);
        printWeghtedFreq(category, wfreq, annotator);
        
        Map<String, Double> freq = wk.getFreq(annotator);
        printFreq(category, freq, annotator);
    }
    
    private void printFreq(List<String> category, Map<String, Double> freq, String annotator)
    {
        StringBuilder sb = new StringBuilder();
        for (String c : category)
        {
            sb.append(MessageFormat.format("{0}, {1, number, #.####}\n", c, freq.get(c)));
        }
        FootPrint.info("Frequency of category for " + annotator + "\n" + sb.toString());
    }
    
    private void printWeghtedFreq(List<String> category, Map<String, Double> wfreq, String annotator)
    {
        StringBuilder sb = new StringBuilder();
        for (String c : category)
        {
            sb.append(MessageFormat.format("{0}, {1, number, #.####}\n", c, wfreq.get(c)));
        }
        FootPrint.info("Weighted Frequency of category for " + annotator + "\n" + sb.toString());
    }
    
    private String bareId(String dialogueId)
    {
        String[] parts = dialogueId.split("-");
        int index = parts.length - 1;
        String bareId = parts[index];
        if (bareId.length() == 0)
        {
            bareId = parts[index - 1];
        }
        return bareId;
    }
    
    /**
     * PENDING: Get the system ID from a file name.
     * 
     * @param fileName
     * @return
     */
    private String systemId(String fileName)
    {
        String[] parts = fileName.split("\\.");
        parts = parts[0].split("_");
        return parts[1];
    }
    
    /**
     * Analyze inter annotator agreement.
     */
    public class InterAnnotatorAgreementComand extends  AbstractCommand
    {
        public final static String NAME = "iaa";
        
        public InterAnnotatorAgreementComand()
        {
            super("Analyze inter annotator agreement.");
        }

        @Override
        public void perform(Map<String, Object> parameters)
        {
            FootPrint.info("Loading data...");
            loadData(inputPath);
            mergeData(targetErrorCategory);
           
            Map<String, AnnotationMatrix<String, String>> annotations = annotationDataset.getAnnotationMatrices(Arrays.asList(new String[] {annotator1, annotator2}));
            WeightedKappa<String, String> wk = new WeightedKappa<>(annotations);
            double agreement = wk.getAgreement();
            double kappa  = wk.getKappa();       
            FootPrint.show("Agreement: " + DoubleFormat.round(agreement, -3));
            FootPrint.show("Weighted Kappa: " + DoubleFormat.round(kappa, -3));
            
            printFreq(wk, annotator1);
            printFreq(wk, annotator2);
            
            wk.getConfusionMatrix().show();
        }
    }
    
    /**
     * Analyze inter annotator agreements betwen all pairs of annotators.
     */
    public class AllInterAnnotatorAgreementsComand extends  AbstractCommand
    {
        public final static String NAME = "all-iaa";
        
        public AllInterAnnotatorAgreementsComand()
        {
            super("Analyze inter annotator agreement over all of pairs of annotators.");
        }
        
        @Override
        public void perform(Map<String, Object> parameters)
        {
            FootPrint.info("Loading data...");
            loadData(inputPath);
            mergeData(targetErrorCategory);
            
           StringBuilder sbAgreements = new StringBuilder("," + concat(targetAnnotatorList, ","));
           StringBuilder sbKappas = new StringBuilder("," + concat(targetAnnotatorList, ","));
           List<Double> agreements = new ArrayList<>();
           List<Double> kappas = new ArrayList<>();
           for (int i = 0; i < targetAnnotatorList.size(); i++)
            {
                annotator1 = targetAnnotatorList.get(i);
                sbAgreements.append("\n").append(annotator1);
                sbKappas.append("\n").append(annotator1);
                for (int k = -1; k < i; k++)
                {
                    sbAgreements.append(",");
                    sbKappas.append(",");
                }
                for (int j = i + 1; j < targetAnnotatorList.size(); j++)
                {
                    annotator2 = targetAnnotatorList.get(j);
                    Map<String, AnnotationMatrix<String, String>> annotations = annotationDataset.getAnnotationMatrices(Arrays.asList(new String[] {annotator1, annotator2}));
                    WeightedKappa<String, String> wk = new WeightedKappa<>(annotations);
                    double agreement = wk.getAgreement();
                    double kappa  = wk.getKappa();       
                    sbAgreements.append("," + DoubleFormat.round(agreement, -3));
                    sbKappas.append("," + DoubleFormat.round(kappa, -3));
                    agreements.add(agreement);
                    kappas.add(kappa);
                }
            }
            FootPrint.show("Agreements:\n" + sbAgreements.toString());
            VarianceEstimator<Double> agreementsVariance =  new VarianceEstimator<>(agreements);
            FootPrint.show("Average and deviation of Agreements: {0} ± {1}", agreementsVariance.getAverage(), agreementsVariance.getUnbiasedDeviation());
            
            FootPrint.show("Cohen's Kappa coefficients:\n" + sbKappas.toString());
            VarianceEstimator<Double> kappasVariance =  new VarianceEstimator<>(kappas);
            FootPrint.show("Average and deviation of Cohen''s Kappa coefficients: {0} ± {1}", kappasVariance.getAverage(), kappasVariance.getUnbiasedDeviation());
        }
    }
    
    public class MultiAnnotatorAgreementCommand extends AbstractCommand
    {
        public final static String NAME = "maa";
        
        public MultiAnnotatorAgreementCommand()
        {
            super("Computes a confusion matrix.");
        }
        
        @Override
        public void perform(Map<String, Object> parameters)
        {
            FootPrint.show("Loading data...");
            loadData(inputPath);
            mergeData(targetErrorCategory);
            
            StringBuilder sb = new StringBuilder("Loaded Data Statistics:");
            for (String sid : dialogueSessionAndTurnId.keySet())
            {
                Map<String, List<Integer>> dialogueSessions = dialogueSessionAndTurnId.get(sid);
                int numAnnotations = 0;
                for (String dialogueId : dialogueSessions.keySet())
                {
                    numAnnotations += dialogueSessions.get(dialogueId).size();
                }
                sb.append(MessageFormat.format("\n{0}: #Sessions: {1}, #Annotations: {2}", sid, dialogueSessions.size(), numAnnotations));
            }
            FootPrint.info(sb.toString());

            
            Map<String, AnnotationMatrix<String, String>> annotations = annotationDataset.getAnnotationMatrices(targetAnnotatorList);
            WeightedFleissKappa<String, String> wfk = new WeightedFleissKappa<String, String>(annotations, errorCategoryMap.get(targetErrorCategory));
            double agreement = wfk.getAgreement();
            double kappa  = wfk.getKappa();       
            FootPrint.show("Agreement: " + DoubleFormat.round(agreement, -2));
            FootPrint.show("Weigted Fleiss' Kappa: " + DoubleFormat.round(kappa, -2));
            VarianceEstimator<Double> labelCarinality = wfk.getLabelCardinarity();
            VarianceEstimator<Double> labelDensity = wfk.getLabelDensity();
            FootPrint.info("Average label cardinality: {0} ± {1}", labelCarinality.getAverage(), labelCarinality.getUnbiasedDeviation());
            FootPrint.info("Average label density: {0,number,0.000} ± {1,number,0.000}", labelDensity.getAverage(), labelDensity.getUnbiasedDeviation());
            
            for (String annotator : targetAnnotatorList)
            {
                printFreq(wfk, annotator);
            }
            
            Map<String, Double> averageFreq = wfk.getAveragedFreq();
            printFreq(wfk.getCategory(), averageFreq, "Average Frequency");
            
            Map<String, Double> averageWeightedFreq = wfk.getAverageWeightedFreq();
            printFreq(wfk.getCategory(), averageWeightedFreq, "Average Weighted Frequency");
            
            Map<String, Double> cofreq = wfk.getCofreq();
            printFreq(wfk.getCategory(), cofreq, "Cofrequency of ALL");
            
            ConfusionMatrix<String> cm = wfk.getConfusionMatrix();
            FootPrint.info("Confusion matrix\n" + cm.toString(-3));
            double entireSum = cm.getEntireSum();
            double diagonalSum = cm.getDiagonalSum();
            double nonDiagonalSum = cm.getNonDiagonalSum();
            FootPrint.info("Dialonal sum rate: [{0}], Non-diagonal sum rate: [{1}]", diagonalSum/entireSum, nonDiagonalSum/entireSum);
            
            FootPrint.info("Confusion matrix transformed into a doubly stochastic matrix.");
            IndexedMatrix<String, String> dsm = cm.getDoublyStochasticMatrix();
            dsm.show(-3);
            
            FootPrint.info("Confusion matrix whose diagonal is normalized.");
            IndexedMatrix<String, String> vm = cm.getVariationMatrix();
            vm.show(-3);
        }
    }
    
    static class BreakDownAnnotatedTurn
    {
        public Map<String, String> dbCatsMap;
        
        public Map<String, String[]> dbCatsArrayMap;
        
        public int numAnnotations;
        
        public int numOs;
        
        public int numTs;
        
        public int numXs;
        
        public String bdCats;
        
        public String[] bdCatsArray;

        public String speaker;

        public String time;

        public int turnIndex;
        
        public String utterance;
        
        public BreakDownAnnotatedTurn()
        {
            dbCatsMap = new HashMap<>();
            dbCatsArrayMap = new HashMap<>();
            bdCats = "";
        }
        
        public BreakDownAnnotatedTurn(Utterance u)
        {
            this();
            speaker = u.getValue(FieldName.Speaker);
            time = DateTimeFormat.format((Date)u.getValue(FieldName.Time));
            turnIndex = u.getValue(FieldName.TurnIndex);
            utterance = u.getValue(FieldName.Utterance);
        }
        
        public boolean isBreakdown()
        {
          BreakDownAnnotatedUtterance u = new BreakDownAnnotatedUtterance();
          u.setValue(FieldName.NumAnnotation, numAnnotations);
          u.setValue(FieldName.NumO, numOs);
          u.setValue(FieldName.NumT, numTs);
          u.setValue(FieldName.NumX, numXs);
          return BreakdownClassifier.isBreakdown(u);
        }
    }
    
    public static <T> String concat(List<T> list, String delimiter)
    {
        if (list == null)
        {
            return "null";
        }
        else
        {
            StringBuilder ret = new StringBuilder();
            for (int i = 0; i < list.size(); i++)
            {
                if (i > 0)
                {
                    ret.append(delimiter);
                }
                ret.append(list.get(i));
            }
            return ret.toString();
        }
    }


    public static void main(String[] args)
    {
        int exitCode = new CommandLine(new IAATool()).execute(args);
        System.exit(exitCode);
    }
}
