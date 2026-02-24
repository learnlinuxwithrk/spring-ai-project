package com.spring.ai.firstproject;

import com.spring.ai.firstproject.helper.Helper;
import com.spring.ai.firstproject.helper.Pmjvk;
import com.spring.ai.firstproject.helper.Umeed;
import com.spring.ai.firstproject.helper.pmvikas;
import com.spring.ai.firstproject.service.ChatService;
import com.spring.ai.firstproject.service.DataLoader;
import com.spring.ai.firstproject.service.DataTransformer;

import org.junit.jupiter.api.Test;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
class FirstProjectApplicationTests {

	@Autowired
	private ChatService chatService;

	@Autowired
	private DataLoader dataLoader;

	@Autowired
	private DataTransformer dataTransformer;

	@Autowired
	private VectorStore vectorStore;

//   @Test
//   void saveDataToVectorDatabase(){
//       System.out.println("saving data to database");
//   //  this.chatService.saveData(Helper.getData());
//     // this.chatService.saveData(Pmjvk.getData());
//    //  this.chatService.saveData(pmvikas.getData());
//        //this.chatService.saveData(Umeed.getData());
//        System.out.println("data is saved successfully");
//  }

//
	@Test
	void testDataLoader() {



		//Import JSON File
		var documents = dataLoader.ingestJsonIntoVectorStore(vectorStore);

		//import PDF Docs
		//var documents = dataLoader.ingestPdfIntoVectorStore(vectorStore);

		//System.out.println("JSON COUNT " + docCount);
//        var documents = dataLoader.loadDocumentsFromJson();
        //System.out.println("Document Size : "+documents.size());
//
        documents.forEach(item -> {
            System.out.println("ITEM "+item);
        });
//

	}

//    @Test
//    void testPdfDataLoader() {
//        List<Document> documents = this.dataLoader.loadDocumentsFromPdf();
//        System.out.println(documents.size());
//        documents.forEach(item -> {
//            System.out.println(item);
//            System.out.println("__________________-");
//        });
//
//        IO.println("Read__now going to transform");
//
//        var transformedDocument = this.dataTransformer.transform(documents);
//        System.out.println(transformedDocument.size());
//
////        going to save the data into database
//
//        this.vectorStore.add(transformedDocument);
//        System.out.println("Done");
//
//
//    }

}
