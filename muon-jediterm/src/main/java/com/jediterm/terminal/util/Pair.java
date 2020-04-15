/*
 * Copyright 2000-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jediterm.terminal.util;

////import org.jetbrains.annotations.NotNull;

public class Pair<A, B> {
  public final A first;
  public final B second;

  //
  public static <A, B> Pair<A, B> create(A first, B second) {
    return new Pair<A, B>(first, second);
  }

  public static <T> T getFirst(Pair<T, ?> pair) {
    return pair != null ? pair.first : null;
  }

  public static <T> T getSecond(Pair<?, T> pair) {
    return pair != null ? pair.second : null;
  }

  @SuppressWarnings("unchecked")
  private static final Pair EMPTY = create(null, null);

  @SuppressWarnings("unchecked")
  public static <A, B> Pair<A, B> empty() {
    return EMPTY;
  }

  public Pair(A first, B second) {
    this.first = first;
    this.second = second;
  }

  public final A getFirst() {
    return first;
  }

  public final B getSecond() {
    return second;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Pair pair = (Pair)o;

    if (first != null ? !first.equals(pair.first) : pair.first != null) return false;
    if (second != null ? !second.equals(pair.second) : pair.second != null) return false;

    return true;
  }

  public int hashCode() {
    int result = first != null ? first.hashCode() : 0;
    result = 31 * result + (second != null ? second.hashCode() : 0);
    return result;
  }

  public String toString() {
    return "<" + first + "," + second + ">";
  }
}
