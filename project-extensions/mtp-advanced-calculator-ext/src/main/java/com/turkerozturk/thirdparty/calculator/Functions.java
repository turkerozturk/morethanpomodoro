/*
 * This file is part of the MoreThanPomodoro project.
 * Please refer to the project's README.md file for additional details.
 * https://github.com/turkerozturk/morethanpomodoro
 *
 * Copyright (c) 2025 Turker Ozturk
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/gpl-3.0.en.html>.
 */
package com.turkerozturk.thirdparty.calculator;
/*
 * This file is copied from the Advanced Calculator Made in Java Swing
 * (https://github.com/Swif7ify/Advanced-Calculator-Made-in-Java-Swing),
 * specifically from commit:
 * https://github.com/Swif7ify/Advanced-Calculator-Made-in-Java-Swing/commit/8726fda81e55af03cbe4cf972d1f1981fa19bd37
 *
 * The original code is licensed under the MIT License:
 *
 *  --- Begin MIT License Notice ---
 *  MIT License
 *
 *  Copyright (c) 2024 Swif7ify
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *  --- End MIT License Notice ---
 */
import java.util.ArrayList;

public class Functions {
	public static long factorial(double firstValue) {
		if(firstValue < 0) {
			 throw new IllegalArgumentException("Input must be a non-negative integer.");
		}
		long Answer;
		if(firstValue == 0 || firstValue == 1) {
			Answer = 1;
		} else {
			Answer = 1;
			for (int i = 2; i <= firstValue; i++) {
				Answer *= i; 
			}
		}
		return Answer;
	}
	
	public static long sumFactorial(double firstValue, double secondValue) {
		return Functions.factorial(firstValue) + Functions.factorial(secondValue);
	}
	
	public static long divideFactorial(double firstValue, double secondValue) {
		return Functions.factorial(firstValue) / Functions.factorial(secondValue);
	}
	
	public static long summation(String equation, double firstValue, double secondValue, double thirdValue) {
		long start = (long) firstValue;
		long end = (long) secondValue;
		long constant = (long) thirdValue;
		long sum = 0;
		
		switch(equation) {
			case "Cx":
				for (long n = start; n <= end; n++) {
					sum += constant * n;
				}
				break;
				
			case "x+C":
				for (long n = start; n <= end; n++) {
					sum += n + constant;
				}
				break;
				
			case "x^C":
				for (long n = start; n <= end; n++) {
					sum += Math.pow(n, constant);
				}
				break;
			
			default:
				if (constant == 0 || constant == 1) {
					for (long n = start; n <= end; n++) {
						sum += n;
					}
				} else {
					for (long n = start; n <= end; n++) {
						sum +=constant;
		        	}
				}
		}
		return sum;
	}
	
	public static long doubleSummation(int nValue, String equation, double firstValue, double secondValue, double thirdValue, double fourthValue) {
		long start = (long) firstValue;
		long end = (long) secondValue;
		long jstart = (long) thirdValue;
		long jend = (long) fourthValue;
		long sum = 0;
			
		nValue = (nValue == 0) ? 1 : nValue;
		
		for(long n = start; n <= end; n++) {
			for(long j = jstart; j <= jend; j++) {
				switch (equation) {
					case "xy":
						sum += (nValue * n * j);
						break;
					case "x+y":
						sum += (nValue * n + j);
						break;
					case "x^y":
						sum += (nValue * Math.pow(n, j));
						break;
					default:
						sum += n * j;
				 }
			}
		}
		return sum;
	}
	
	public static long prodnot(String equation, double firstValue, double secondValue, double thirdValue) {
		long start = (long) firstValue;
		long end = (long) secondValue;
		long constant = (long) thirdValue;
		long sum = 1;
		
		switch(equation) {
			case "Cx":
				for (long n = start; n <= end; n++) {
					sum *= constant * n;
				}
				break;
				
			case "x+C":
				for (long n = start; n <= end; n++) {
					sum *= n + constant;
				}
				break;
				
			case "x^C":
				for (long n = start; n <= end; n++) {
					sum *= Math.pow(n, constant);
				}
				break;
			
			default:
				if (constant == 0 || constant == 1) {
					for (long n = start; n <= end; n++) {
						sum *= n;
					}
				} else {
					for (long n = start; n <= end; n++) {
						sum *= constant;
		        	}
				}
		}
		
		return sum;
	}
	
	public static long doubleProdNot(int nValue, String equation, double firstValue, double secondValue, double thirdValue, double fourthValue) {
		long start = (long) firstValue;
		long end = (long) secondValue;
		long jstart = (long) thirdValue;
		long jend = (long) fourthValue;
		long sum = 1;
		
		nValue = (nValue == 0) ? 1 : nValue;
		
		for(long n = start; n <= end; n++) {
			for(long j = jstart; j <= jend; j++) {
				switch (equation) {
					case "xy":
						sum *= (nValue * n * j);
						break;
					case "x+y":
						sum *= (nValue * n + j);
						break;
					case "x^y":
						sum *= (nValue * Math.pow(n, j));
						break;
					default:
						sum *= n * j;
				 }
			}
		}
		return sum;
	}
	
	public static double basicCalculation(String operator, double firstValue, double secondValue) {
		double Answer = 0;
		switch(operator) {
		case "//":
			Answer = firstValue / secondValue;
			Answer = (long) Answer;
			break;
		case "%":
			Answer = firstValue % secondValue;
			break;
		case "x^y":
			Answer = Math.pow(firstValue, secondValue);
			break;
		case "numroot":
			Answer = Math.pow(secondValue, 1.0 / firstValue);
		}
		
		return Answer;
	}
	
	public static double calculateResult(ArrayList<Double> numbers,  ArrayList<String> operators) {
	    double result = numbers.get(0);
	    
	    for (int i = 1; i < numbers.size(); i++) {
	    	String operator = operators.get(0);
	    	switch(operator) {
	            case "+":
	                result += numbers.get(i);
	                break;
	            case "-":
	                result -= numbers.get(i);
	                break;
	            case "*":
	                result *= numbers.get(i);
	                break;
	            case "÷":
	                result /= numbers.get(i);
	                break;
	        }
	        operators.remove(0);
	    }

	    return result;
	}
	
	public static String formatString(double Answer) {
	    String formattedAnswer;
	    if (Answer % 1 == 0) {
	        formattedAnswer = String.valueOf((long) Answer);
	    } else {
	        formattedAnswer = String.format("%.6f", Answer).replaceAll("0*$", "").replaceAll("\\.$", "");
	    }
	    return formattedAnswer;
	}
}