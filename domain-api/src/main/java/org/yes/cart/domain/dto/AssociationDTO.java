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

package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;

/**
 * Product asssociation interface. At this moment supported
 * accessories, up sell, cross sell, buy with
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface AssociationDTO extends Identifiable {

    /**
     * @return pkimary key.
     */
    long getAssociationId();

    /**
     * @param associationId primary key to set
     */
    void setAssociationId(long associationId);

    /**
     * Identifiable human readable association code.
     *
     * @return unique human readable association code
     */
    String getCode();

    /**
     * @param code unique human readable association code to use.
     */
    void setCode(String code);

    /**
     * @return Association name.
     */
    String getName();

    /**
     * @param name association name.
     */
    void setName(String name);

    /**
     * @return description
     */
    String getDescription();

    /**
     * @param description description to use.
     */
    void setDescription(String description);

}
