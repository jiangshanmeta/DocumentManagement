import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DocumentManagementSystemTest {
    private static final String RESOURCES = "src" + File.separator + "test" + File.separator + "resources" + File.separator;
    private static final String LETTER = RESOURCES + "patient.letter";
    private static final String REPORT = RESOURCES + "patient.report";
    private static final String SNAPSHOT = RESOURCES + "snapshot.jpg";
    private static final String INVOICE = RESOURCES + "patient.invoice";
    private static final String JOE_BLOGGS = "Joe Bloggs";

    private DocumentManagementSystem system = new DocumentManagementSystem();

    @Test
    public void shouldImportFile() throws IOException {
        system.importFile(LETTER);
        final Document document = onlyDocument();

        assertAttributeEqual(document,Attributes.PATH,LETTER);
    }

    @Test
    public void shouldImportLetterAttributes() throws IOException {
        system.importFile(LETTER);
        final Document document = onlyDocument();

        assertAttributeEqual(document,Attributes.PATIENT,JOE_BLOGGS);
        assertAttributeEqual(document,Attributes.ADDRESS,"123 Fake Street\n" +
                "Westminster\n" +
                "London\n" +
                "United Kingdom");

        assertAttributeEqual(document,Attributes.BODY,"We are writing to you to confirm the re-scheduling of your appointment\n" +
                "with Dr. Avaj from 29th December 2016 to 5th January 2017.");

        assertTypeIs("LETTER",document);
    }

    @Test
    public void shouldImportReportAttribute() throws IOException {
        system.importFile(REPORT);
        assertIsReport(onlyDocument());
    }

    @Test
    public void shouldImportImageAttributes() throws IOException {
        system.importFile(SNAPSHOT);

        final Document document = onlyDocument();

        assertAttributeEqual(document,Attributes.WIDTH,"1191");
        assertAttributeEqual(document,Attributes.HEIGHT,"863");
        assertTypeIs("IMAGE",document);
    }

    @Test
    public void shouldImportInvoiceAttributes() throws IOException {
        system.importFile(INVOICE);
        final Document document = onlyDocument();
        assertAttributeEqual(document,Attributes.PATIENT,JOE_BLOGGS);
        assertAttributeEqual(document,Attributes.AMOUNT,"$100");
        assertTypeIs("INVOICE",document);
    }

    @Test
    public void shouldBeAbleToSearchFilesByAttributes() throws IOException {
        system.importFile(LETTER);
        system.importFile(REPORT);
        system.importFile(SNAPSHOT);

        final List<Document> documents = system.search("patient:Joe,body:Diet Coke");

        assertEquals(1,documents.size());
        assertIsReport(documents.get(0));
    }

    @Test(expected = FileNotFoundException.class)
    public void shouldNotImportMissingFile() throws Exception {
        system.importFile(RESOURCES +"notExist.letter");
    }

    @Test(expected = UnknownFileTypeException.class)
    public void shouldNotImportUnknownFileType() throws Exception{
        system.importFile(RESOURCES + "unknown.txt");
    }

    @Test(expected = UnknownFileTypeException.class)
    public void shouldNotImportFileWithoutExt() throws Exception{
        system.importFile(RESOURCES + "withoutExt");
    }

    @Test(expected = UnknownFileTypeException.class)
    public void shouldNotImportFileWithExtEmpty() throws Exception {
        system.importFile(RESOURCES+ "wrongExt.");
    }


    private void assertIsReport(final Document document){
        assertAttributeEqual(document,Attributes.PATIENT,JOE_BLOGGS);
        assertAttributeEqual(document,Attributes.BODY,"On 5th January 2017 I examined Joe's teeth.\n" +
                "We discussed his switch from drinking Coke to Diet Coke.\n" +
                "No new problems were noted with his teeth.");

        assertTypeIs("REPORT",document);
    }

    private void assertAttributeEqual(
            final Document document,
            final String attributeName,
            final String expectedValue
    ){
        assertEquals("Document has the wrong value for " + attributeName , expectedValue, document.getAttribute(attributeName));
    }

    private Document onlyDocument(){
        final List<Document> documents = system.contents();
        assertEquals(1,documents.size());

        return documents.get(0);
    }

    private void assertTypeIs(final String type, final Document document){
        assertAttributeEqual(document,Attributes.TYPE,type);
    }


}
