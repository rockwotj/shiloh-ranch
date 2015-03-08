/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2015-01-14 17:53:03 UTC)
 * on 2015-03-07 at 23:28:53 UTC 
 * Modify at your own risk.
 */

package com.appspot.shiloh_ranch.api.model;

/**
 * Model definition for Deletion.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the shilohranch. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class Deletion extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("deletion_key")
  private java.lang.String deletionKey;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String entityKey;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String kind;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("time_added")
  private java.lang.String timeAdded;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getDeletionKey() {
    return deletionKey;
  }

  /**
   * @param deletionKey deletionKey or {@code null} for none
   */
  public Deletion setDeletionKey(java.lang.String deletionKey) {
    this.deletionKey = deletionKey;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getEntityKey() {
    return entityKey;
  }

  /**
   * @param entityKey entityKey or {@code null} for none
   */
  public Deletion setEntityKey(java.lang.String entityKey) {
    this.entityKey = entityKey;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getKind() {
    return kind;
  }

  /**
   * @param kind kind or {@code null} for none
   */
  public Deletion setKind(java.lang.String kind) {
    this.kind = kind;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getTimeAdded() {
    return timeAdded;
  }

  /**
   * @param timeAdded timeAdded or {@code null} for none
   */
  public Deletion setTimeAdded(java.lang.String timeAdded) {
    this.timeAdded = timeAdded;
    return this;
  }

  @Override
  public Deletion set(String fieldName, Object value) {
    return (Deletion) super.set(fieldName, value);
  }

  @Override
  public Deletion clone() {
    return (Deletion) super.clone();
  }

}