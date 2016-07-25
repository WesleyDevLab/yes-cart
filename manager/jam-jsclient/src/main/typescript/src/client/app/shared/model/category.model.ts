/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

export interface CategoryVO {

  categoryId : number;

  parentId : number;

  rank : number;

  productTypeId : number;

  productTypeName : string;

  name : string;

  guid  : string;

  displayNames :  any;

  description : string;

  uitemplate  : string;

  availablefrom : Date;

  availableto : Date;

  uri  : string;

  title  : string;

  metakeywords  : string;

  metadescription  : string;

  displayTitles : any;

  displayMetakeywords : any;

  displayMetadescriptions : any;

  navigationByAttributes : boolean;

  navigationByBrand : boolean;

  navigationByPrice : boolean;

  navigationByPriceTiers  : string;

  children : Array<CategoryVO>;

}