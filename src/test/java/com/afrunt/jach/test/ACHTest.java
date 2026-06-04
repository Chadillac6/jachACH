/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.afrunt.jach.test;

import com.afrunt.jach.ACH;
import com.afrunt.jach.document.ACHDocument;
import com.afrunt.jach.domain.GeneralBatchHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Andrii Frunt
 */
public class ACHTest {

    @ParameterizedTest(name = "read/write round-trip: {0}")
    @DisplayName("ACH files survive a read-write round-trip unchanged")
    @ValueSource(strings = {
            "ach-ppd-contested-dishonored-return.txt",
            "ach-ppd-dishonored-return.txt",
            "ach-ppd-return.txt",
            "ach-cor2.txt",
            "ach-return-cor.txt",
            "cie-returns.txt",
            "ach-return.txt",
            "ach-tr.txt",
            "ach-payrol.txt",
            "ach-web-ppd.txt",
            "ach-pos.txt"
    })
    void testReadWrite(String achFileName) {
        ACH ach = new ACH();
        ACHDocument document = ach.read(getClass().getClassLoader().getResourceAsStream(achFileName));
        String out = ach.write(document);
        testFilesAreEquals(getClass().getClassLoader().getResourceAsStream(achFileName), new ByteArrayInputStream(out.getBytes()));
    }

    @Test
    @DisplayName("Block aligning pads output to multiples of 10 lines")
    void testBlockAligning() {
        ACH ach = new ACH()
                .withBlockAligning(true);

        ACHDocument document = ach.read(getClass().getClassLoader().getResourceAsStream("ach-payrol.txt"));
        String out = ach.write(document);
        String[] strings = out.split(ACH.LINE_SEPARATOR);
        assertEquals(10, strings.length);

        document = ach.read(getClass().getClassLoader().getResourceAsStream("ach-pos.txt"));
        out = ach.write(document);
        strings = out.split(ACH.LINE_SEPARATOR);
        assertEquals(10, strings.length);
    }

    @Test
    @DisplayName("GeneralBatchHeader fluent cast API works correctly")
    void castTest() {
        GeneralBatchHeader batchHeader = new GeneralBatchHeader()
                .setCompanyID("")
                .setServiceClassCode("XXX")
                .cast(GeneralBatchHeader.class)
                .setCompanyName("")
                .setBatchNumber(1)
                .cast();
    }

    @Test
    @DisplayName("Reading an empty stream returns a document with no batches")
    void testReadEmptyStream() {
        ACH ach = new ACH();
        InputStream emptyStream = new ByteArrayInputStream(new byte[0]);
        ACHDocument document = ach.read(emptyStream);
        assertNotNull(document);
        assertTrue(document.getBatches().isEmpty());
    }

    @Test
    @DisplayName("Reading a null stream throws NullPointerException")
    void testReadNullStream() {
        ACH ach = new ACH();
        assertThrows(NullPointerException.class, () -> ach.read((InputStream) null));
    }

    @Test
    @DisplayName("Reading a malformed ACH file throws an appropriate exception")
    void testReadMalformedFile() {
        ACH ach = new ACH();
        String malformedContent = "This is not a valid ACH file\nNeither is this line\n";
        InputStream malformedStream = new ByteArrayInputStream(malformedContent.getBytes());
        assertThrows(Exception.class, () -> ach.read(malformedStream));
    }

    private void testFilesAreEquals(InputStream is1, InputStream is2) {
        Scanner sc1 = new Scanner(is1, ACH.DEFAULT_CHARSET.name());
        Scanner sc2 = new Scanner(is2, ACH.DEFAULT_CHARSET.name());

        while (sc1.hasNextLine()) {
            String line1 = sc1.nextLine();
            if (line1.trim().equals("")) {
                continue;
            }
            String line2 = sc2.nextLine();
            assertEquals(line1, line2);
        }
    }
}
