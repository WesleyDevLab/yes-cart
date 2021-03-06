/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.web.shoppingcart.log;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/12/11
 * Time: 6:19 PM
 */
public class MultipleLogTest {

    @Test
    public void testMultipleLogFiles() throws IOException {
        Logger log20 = LoggerFactory.getLogger("SHOP20");
        log20.info("shop20");
        Logger log10 = LoggerFactory.getLogger("SHOP10");
        log10.info("shop10");
        String log10content = readFileAsString("target/shop10.log");
        assertTrue(log10content.indexOf("shop10") > -1);
        assertTrue(log10content.indexOf("shop20") == -1);
        String log20content = readFileAsString("target/shop20.log");
        assertTrue(log20content.indexOf("shop20") > -1);
        assertTrue(log20content.indexOf("shop10") == -1);
    }

    private static String readFileAsString(String filePath) throws IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(
                new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }
}
