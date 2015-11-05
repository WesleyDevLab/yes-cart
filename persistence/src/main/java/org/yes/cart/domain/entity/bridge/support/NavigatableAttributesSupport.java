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

package org.yes.cart.domain.entity.bridge.support;

import java.util.Set;

/**
 * User: denispavlov
 * Date: 16/11/2014
 * Time: 17:07
 */
public interface NavigatableAttributesSupport {

    /**
     * Get all navigatable attribute codes (+ brands, price, query and tag).
     *
     * @return set of attribute codes.
     */
    Set<String> getAllNavigatableAttributeCodes();

    /**
     * Get all searchable attribute codes (navigatable are not included).
     *
     * @return set of attribute codes.
     */
    Set<String> getAllSearchableAttributeCodes();

    /**
     * Get all storable attribute codes.
     *
     * @return set of attribute codes.
     */
    Set<String> getAllStorableAttributeCodes();

}
