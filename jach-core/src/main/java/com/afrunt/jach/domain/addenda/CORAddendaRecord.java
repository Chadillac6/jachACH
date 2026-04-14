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
package com.afrunt.jach.domain.addenda;

import com.afrunt.jach.annotation.ACHField;
import com.afrunt.jach.annotation.ACHRecordType;

import static com.afrunt.jach.annotation.InclusionRequirement.*;

/**
 * @author Andrii Frunt
 */
@SuppressWarnings("WeakerAccess")
@ACHRecordType(name = "Notification Of Change (NOC) - Addenda Record")
public class CORAddendaRecord extends BaseCORAddendaRecord {

    public static final String CHANGE_CODE = "Change Code";

    private String changeCode;

    @ACHField(start = 3, length = 3, inclusion = MANDATORY, name = CHANGE_CODE)
    public String getChangeCode() {
        return changeCode;
    }

    public CORAddendaRecord setChangeCode(String changeCode) {
        this.changeCode = changeCode;
        return this;
    }

    @ACHField(start = 64, length = 15, inclusion = BLANK, name = RESERVED)
    public String getReserved2() {
        return reserved(15);
    }
}
