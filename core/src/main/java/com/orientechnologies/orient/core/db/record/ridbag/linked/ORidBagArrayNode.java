/*
 * Copyright 2018 OrientDB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.orientechnologies.orient.core.db.record.ridbag.linked;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.serialization.serializer.record.binary.BytesContainer;
import com.orientechnologies.orient.core.serialization.serializer.record.binary.HelperClasses;
import com.orientechnologies.orient.core.serialization.serializer.record.binary.OVarIntSerializer;
import com.orientechnologies.orient.core.storage.OPhysicalPosition;

/**
 *
 * @author mdjurovi
 */
class ORidBagArrayNode extends ORidbagNode{
  
  protected static byte RIDBAG_ARRAY_NODE_TYPE = 'a';
  private OIdentifiable[] rids;  
  
  @Override
  protected byte getNodeType(){
    return RIDBAG_ARRAY_NODE_TYPE;
  }
  
  protected ORidBagArrayNode(long physicalPosition, boolean initContainer, boolean considerLoaded) {
    super(physicalPosition, considerLoaded);
    if (initContainer){
      rids = new OIdentifiable[1];
    }
  }
  
  protected ORidBagArrayNode(long physicalPosition, int initialCapacity, boolean considerLoaded){
    super(physicalPosition, considerLoaded);
    rids = new OIdentifiable[initialCapacity];
  }
  
  @Override
  protected int capacity(){
    return rids.length;
  }    
    
  @Override
  protected void addInternal(OIdentifiable value){
    rids[currentIndex] = value;
  }
  
  @Override
  protected void addAllInternal(OIdentifiable[] values){
    System.arraycopy(values, 0, rids, currentIndex, values.length);
  }
  
  @Override
  protected OIdentifiable getAt(int index){
    return rids[index];
  }
  
  @Override
  protected boolean remove(OIdentifiable value){      
    for (int i = 0; i < rids.length; i++){
      OIdentifiable val = rids[i];
      if (val.equals(value)){
        //found so remove it
        //first shift all
        for (int j = i + 1; j < rids.length; j++){
          rids[j - 1] = rids[j];
        }
        --currentIndex;
        return true;
      }
    }

    return false;
  }
  
  @Override
  protected boolean contains(OIdentifiable value){
    for (int i = 0; i < rids.length; i++){
      OIdentifiable val = rids[i];
      if (val.equals(value)){
        return true;
      }
    }

    return false;
  }
  
  @Override
  protected boolean isTailNode(){
    return false;
//    return capacity() == 1 && currentIndex == 1;
  }
  
  @Override
  protected OIdentifiable[] getAllRids(){
    return rids;
  }
  
  @Override
  protected void setAt(OIdentifiable value, int index){
    rids[index] = value;
  }
  
  @Override
  protected byte[] serializeInternal(){
    BytesContainer container = new BytesContainer();
    OVarIntSerializer.write(container, rids.length);
    for (OIdentifiable value : rids){
      HelperClasses.writeLinkOptimized(container, value);
    }
    return container.fitBytes();
  }
  
  @Override
  protected void addInDeserializeInternal(OIdentifiable value, int index){
    rids[index] = value;
  }
}
