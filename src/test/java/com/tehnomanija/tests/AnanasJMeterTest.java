package com.tehnomanija.tests;

import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jorphan.collections.HashTree;

import java.io.FileOutputStream;

public class AnanasJMeterTest {

    public static void main(String[] args) throws Exception {
        // Postavljanje JMeter konfiguracije
        JMeterUtils.loadJMeterProperties("C:/Users/Korisnik/Desktop/Dokumenta/apache-jmeter-5.6.3/bin/jmeter.properties");

        JMeterUtils.setJMeterHome("C:/Users/Korisnik/Desktop/Dokumenta/apache-jmeter-5.6.3");
        JMeterUtils.initLogging();
        JMeterUtils.initLocale();

        // Kreiraj JMeter engine
        StandardJMeterEngine jmeter = new StandardJMeterEngine();

        // HTTP Sampler za GET zahtev
        HTTPSamplerProxy httpSampler = new HTTPSamplerProxy();
        httpSampler.setDomain("api.ananas.rs");
        httpSampler.setPath("/v1/products");
        httpSampler.setMethod("GET");
        httpSampler.setName("Ananas GET Request");

        // Kreiraj Loop Controller
        LoopController loopController = new LoopController();
        loopController.setLoops(1);  // Broj ponavljanja
        loopController.addTestElement(httpSampler);  // Dodaj HTTP Sampler
        loopController.setFirst(true);  // Prvi u nizu
        loopController.initialize();

        // Kreiraj Thread Group
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName("Thread Group");
        threadGroup.setNumThreads(1); // Broj korisnika
        threadGroup.setRampUp(1); // Ramp-up period
        threadGroup.setSamplerController(loopController);  // Poveži ThreadGroup sa LoopController

        // Kreiraj Test Plan
        TestPlan testPlan = new TestPlan("Ananas API Test Plan");

        // Dodaj Test Plan i Thread Group u test stablo
        HashTree testPlanTree = new HashTree();
        testPlanTree.add(testPlan);
        testPlanTree.add(threadGroup);

        // Sačuvaj Test Plan u .jmx fajl
        FileOutputStream outputStream = new FileOutputStream("C:/Users/Korisnik/Desktop/Dokumenta/apache-jmeter-5.6.3/jmeter_test_plan.jmx");
        SaveService.saveTree(testPlanTree, outputStream);
        outputStream.close();

        // Kreiranje Result Collector-a za beleženje rezultata
        ResultCollector resultCollector = new ResultCollector(new Summariser());
        resultCollector.setFilename("C:/Users/Korisnik/Desktop/Dokumenta/apache-jmeter-5.6.3/results.jtl");
        testPlanTree.add(testPlanTree.getArray()[0], resultCollector);

        // Konfigurisanje JMeter-a
        jmeter.configure(testPlanTree);

        // Pokretanje testa
        jmeter.run();
    }
}
