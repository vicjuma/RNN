
/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
        
package org.apache.poi.util;

import junit.framework.*;

/**
 * Unit test for ArrayUtil
 *
 * @author Nick Burch
 */
public class TestArrayUtil extends TestCase
{
	/**
	 * Test to ensure that our own arraycopy behaves as it should do
	 */
	public void testarraycopy() {
		byte[] bytes = new byte[] { 0x01, 0x02, 0x03, 0x04 };
		
		// Test copy whole thing
		byte[] dest = new byte[4];
		ArrayUtil.arraycopy(bytes, 0, dest, 0, 4);
		
		assertEquals(dest.length, bytes.length);
		for(int i=0; i<dest.length; i++) {
			assertEquals(bytes[i], dest[i]);
		}
		
		// ToDo - test exceptions are as expected
	}
	
	
	/**
	 * Helper for testArrayMoveWithin 
	 */
	private Integer[] getIntsList() { 
		return new Integer[] {
				new Integer(0),
				new Integer(1),
				new Integer(2),
				new Integer(3),
				new Integer(4),
				new Integer(5),
				new Integer(6),
				new Integer(7),
				new Integer(8),
				new Integer(9)
		};
	}
	
	/**
	 * Test to ensure that arrayMoveWithin works as expected
	 */
	public void testArrayMoveWithin() {
		Integer[] ints = getIntsList();
		
		assertEquals(new Integer(0), ints[0]);
		assertEquals(new Integer(1), ints[1]);
		assertEquals(new Integer(2), ints[2]);
		assertEquals(new Integer(3), ints[3]);
		assertEquals(new Integer(4), ints[4]);
		assertEquals(new Integer(5), ints[5]);
		assertEquals(new Integer(6), ints[6]);
		assertEquals(new Integer(7), ints[7]);
		assertEquals(new Integer(8), ints[8]);
		assertEquals(new Integer(9), ints[9]);
		
		
		//
		// Moving to a later point in the array
		//
		
		// Shift 1 back
		ints = getIntsList();
		ArrayUtil.arrayMoveWithin(ints, 4, 8, 1);
		assertEquals(new Integer(0), ints[0]);
		assertEquals(new Integer(1), ints[1]);
		assertEquals(new Integer(2), ints[2]);
		assertEquals(new Integer(3), ints[3]);
		assertEquals(new Integer(5), ints[4]);
		assertEquals(new Integer(6), ints[5]);
		assertEquals(new Integer(7), ints[6]);
		assertEquals(new Integer(8), ints[7]);
		assertEquals(new Integer(4), ints[8]);
		assertEquals(new Integer(9), ints[9]);
		
		// Shift front 1 back
		ints = getIntsList();
		ArrayUtil.arrayMoveWithin(ints, 0, 7, 1);
		assertEquals(new Integer(1), ints[0]);
		assertEquals(new Integer(2), ints[1]);
		assertEquals(new Integer(3), ints[2]);
		assertEquals(new Integer(4), ints[3]);
		assertEquals(new Integer(5), ints[4]);
		assertEquals(new Integer(6), ints[5]);
		assertEquals(new Integer(7), ints[6]);
		assertEquals(new Integer(0), ints[7]);
		assertEquals(new Integer(8), ints[8]);
		assertEquals(new Integer(9), ints[9]);
		
		// Shift 1 to end
		ints = getIntsList();
		ArrayUtil.arrayMoveWithin(ints, 4, 9, 1);
		assertEquals(new Integer(0), ints[0]);
		assertEquals(new Integer(1), ints[1]);
		assertEquals(new Integer(2), ints[2]);
		assertEquals(new Integer(3), ints[3]);
		assertEquals(new Integer(5), ints[4]);
		assertEquals(new Integer(6), ints[5]);
		assertEquals(new Integer(7), ints[6]);
		assertEquals(new Integer(8), ints[7]);
		assertEquals(new Integer(9), ints[8]);
		assertEquals(new Integer(4), ints[9]);

				
		//
		// Moving to an earlier point in the array
		//
		
		// Shift 1 forward
		ints = getIntsList();
		ArrayUtil.arrayMoveWithin(ints, 8, 3, 1);
		assertEquals(new Integer(0), ints[0]);
		assertEquals(new Integer(1), ints[1]);
		assertEquals(new Integer(2), ints[2]);
		assertEquals(new Integer(8), ints[3]);
		assertEquals(new Integer(3), ints[4]);
		assertEquals(new Integer(4), ints[5]);
		assertEquals(new Integer(5), ints[6]);
		assertEquals(new Integer(6), ints[7]);
		assertEquals(new Integer(7), ints[8]);
		assertEquals(new Integer(9), ints[9]);
		
		// Shift end 1 forward
		ints = getIntsList();
		ArrayUtil.arrayMoveWithin(ints, 9, 2, 1);
		assertEquals(new Integer(0), ints[0]);
		assertEquals(new Integer(1), ints[1]);
		assertEquals(new Integer(9), ints[2]);
		assertEquals(new Integer(2), ints[3]);
		assertEquals(new Integer(3), ints[4]);
		assertEquals(new Integer(4), ints[5]);
		assertEquals(new Integer(5), ints[6]);
		assertEquals(new Integer(6), ints[7]);
		assertEquals(new Integer(7), ints[8]);
		assertEquals(new Integer(8), ints[9]);
		
		// Shift 1 to front
		ints = getIntsList();
		ArrayUtil.arrayMoveWithin(ints, 5, 0, 1);
		assertEquals(new Integer(5), ints[0]);
		assertEquals(new Integer(0), ints[1]);
		assertEquals(new Integer(1), ints[2]);
		assertEquals(new Integer(2), ints[3]);
		assertEquals(new Integer(3), ints[4]);
		assertEquals(new Integer(4), ints[5]);
		assertEquals(new Integer(6), ints[6]);
		assertEquals(new Integer(7), ints[7]);
		assertEquals(new Integer(8), ints[8]);
		assertEquals(new Integer(9), ints[9]);

		
		//
		// Moving many to a later point in the array
		//
		
		// Shift 3 back
		ints = getIntsList();
		ArrayUtil.arrayMoveWithin(ints, 2, 6, 3);
		assertEquals(new Integer(0), ints[0]);
		assertEquals(new Integer(1), ints[1]);
		assertEquals(new Integer(5), ints[2]);
		assertEquals(new Integer(6), ints[3]);
		assertEquals(new Integer(7), ints[4]);
		assertEquals(new Integer(8), ints[5]);
		assertEquals(new Integer(2), ints[6]);
		assertEquals(new Integer(3), ints[7]);
		assertEquals(new Integer(4), ints[8]);
		assertEquals(new Integer(9), ints[9]);
		
		// Shift 3 to back
		ints = getIntsList();
		ArrayUtil.arrayMoveWithin(ints, 2, 7, 3);
		assertEquals(new Integer(0), ints[0]);
		assertEquals(new Integer(1), ints[1]);
		assertEquals(new Integer(5), ints[2]);
		assertEquals(new Integer(6), ints[3]);
		assertEquals(new Integer(7), ints[4]);
		assertEquals(new Integer(8), ints[5]);
		assertEquals(new Integer(9), ints[6]);
		assertEquals(new Integer(2), ints[7]);
		assertEquals(new Integer(3), ints[8]);
		assertEquals(new Integer(4), ints[9]);
		
		// Shift from 3 front
		ints = getIntsList();
		ArrayUtil.arrayMoveWithin(ints, 0, 5, 3);
		assertEquals(new Integer(3), ints[0]);
		assertEquals(new Integer(4), ints[1]);
		assertEquals(new Integer(5), ints[2]);
		assertEquals(new Integer(6), ints[3]);
		assertEquals(new Integer(7), ints[4]);
		assertEquals(new Integer(0), ints[5]);
		assertEquals(new Integer(1), ints[6]);
		assertEquals(new Integer(2), ints[7]);
		assertEquals(new Integer(8), ints[8]);
		assertEquals(new Integer(9), ints[9]);
		
		
		//
		// Moving many to an earlier point in the array
		//
		
		// Shift 3 forward
		ints = getIntsList();
		ArrayUtil.arrayMoveWithin(ints, 6, 2, 3);
		assertEquals(new Integer(0), ints[0]);
		assertEquals(new Integer(1), ints[1]);
		assertEquals(new Integer(6), ints[2]);
		assertEquals(new Integer(7), ints[3]);
		assertEquals(new Integer(8), ints[4]);
		assertEquals(new Integer(2), ints[5]);
		assertEquals(new Integer(3), ints[6]);
		assertEquals(new Integer(4), ints[7]);
		assertEquals(new Integer(5), ints[8]);
		assertEquals(new Integer(9), ints[9]);
		
		// Shift 3 to front
		ints = getIntsList();
		ArrayUtil.arrayMoveWithin(ints, 6, 0, 3);
		assertEquals(new Integer(6), ints[0]);
		assertEquals(new Integer(7), ints[1]);
		assertEquals(new Integer(8), ints[2]);
		assertEquals(new Integer(0), ints[3]);
		assertEquals(new Integer(1), ints[4]);
		assertEquals(new Integer(2), ints[5]);
		assertEquals(new Integer(3), ints[6]);
		assertEquals(new Integer(4), ints[7]);
		assertEquals(new Integer(5), ints[8]);
		assertEquals(new Integer(9), ints[9]);
		
		// Shift from 3 back
		ints = getIntsList();
		ArrayUtil.arrayMoveWithin(ints, 7, 3, 3);
		assertEquals(new Integer(0), ints[0]);
		assertEquals(new Integer(1), ints[1]);
		assertEquals(new Integer(2), ints[2]);
		assertEquals(new Integer(7), ints[3]);
		assertEquals(new Integer(8), ints[4]);
		assertEquals(new Integer(9), ints[5]);
		assertEquals(new Integer(3), ints[6]);
		assertEquals(new Integer(4), ints[7]);
		assertEquals(new Integer(5), ints[8]);
		assertEquals(new Integer(6), ints[9]);
		
		
		// Check can't shift more than we have
		try {
			ints = getIntsList();
			ArrayUtil.arrayMoveWithin(ints, 7, 3, 5);
			fail();
		} catch(IllegalArgumentException e) {
			// Good, we don't have 5 from 7 onwards
		}
		
		// Check can't shift where would overshoot
		try {
			ints = getIntsList();
			ArrayUtil.arrayMoveWithin(ints, 2, 7, 5);
			fail();
		} catch(IllegalArgumentException e) {
			// Good, we can't fit 5 in starting at 7
		}
	}
}

