/*
 * Copyright (c) 2009-2012 International Integrated System, Inc.
 * 11F, No.133, Sec.4, Minsheng E. Rd., Taipei, 10574, Taiwan, R.O.C.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. ("Confidential Information").
 */
package com.iisigroup.cap.utils;

import java.math.BigDecimal;

import com.iisigroup.cap.Constants;

/**
 * <pre>
 * CapCommonUtil
 * </pre>
 * 
 * @since 2010/12/29
 * @author RodesChen
 * @version <ul>
 *          <li>2010/12/29,RodesChen,new
 *          <li>2011/11/1,rodeschen,from cap
 *          </ul>
 */
public class CapCommonUtil {

	/**
	 * <pre>
	 * 數字轉國字大寫
	 * </pre>
	 * 
	 * @param amount
	 *            amount
	 * @param nonDollar
	 *            boolean
	 * @return String String
	 */
	public static String toChineseUpperAmount(String amount, boolean nonDollar) {
		if (!nonDollar) {
			amount = CapMath.getBigDecimal(amount).setScale(3, BigDecimal.ROUND_HALF_UP).toString();
		}
		String[] digit = { "零", "壹", "貳", "參", "肆", "伍", "陸", "柒", "捌", "玖" };
		String[] aa = { "", "拾", "佰", "仟" };
		String[] bb = { "", "萬", "億", "兆" };
		String[] cc = { "角", "分", "厘" };
		String[] amtInteger = {};
		String amtFraction = "";
		StringBuffer result = new StringBuffer();

		if (amount.indexOf(".") > 0) {
			amtInteger = formatAmount(amount.substring(0, amount.indexOf(".")), 4).split(",");
			amtFraction = amount.substring(amount.indexOf(".") + 1);
		} else {
			amtInteger = formatAmount(amount, 4).split(",");
		}

		try {
			for (int i = 1; i <= amtInteger.length && i <= bb.length; i++) {
				boolean behindZeroDigit = false;
				StringBuffer ans = new StringBuffer();
				String aThousandAmount = amtInteger[i - 1];
				for (int j = aThousandAmount.length(); j > 0; j--) {
					String d = digit[Integer.parseInt(String.valueOf(aThousandAmount.charAt(aThousandAmount.length() - j)))];
					if (!"零".equals(d)) {
						if (behindZeroDigit) {
							ans.append("零");
						}
						ans.append(d);
						ans.append(aa[(j + 3) % 4]);
					}
					behindZeroDigit = "零".equals(d);
				}
				if (ans.length() > 0) {
					result.append(ans);
					result.append(bb[amtInteger.length - i]);
				}
			}
			// 修正尾數
			if (result.length() == 0) {
				result.append("零");
			}
			if (result.length() > 0) {
				if (!nonDollar) {
					result.append("元");
				}
				if (amtFraction.length() < 1 || (!nonDollar && amtFraction.matches("^000[0-9]{0,}"))) {
					if (!nonDollar) {
						result.append("整");
					}
				} else {
					if (nonDollar) {
						result.append("點");
					}
					int length = amtFraction.length();
					for (int j = 0; j < amtFraction.length() && (nonDollar || (!nonDollar && j < cc.length)); j++) {
						String a = String.valueOf(amtFraction.charAt(j));
						if (!"0".equals(a)) {
							String d = digit[Integer.parseInt(a)];
							result.append(d);
							if (!nonDollar) {
								result.append(cc[(j) % 4]);
							}
						} else if (nonDollar && CapMath.compare(amtFraction.substring(j, length), "0") != 0) {
							result.append("零");
						}
					}
				}
			}

		} catch (Exception e) {
			return Constants.EMPTY_STRING;
		}
		return result.toString();
	}

	public static String formatAmount(String amount, int delimiterDigit) {
		StringBuffer formattedAmount = new StringBuffer();
		int firstTokenLength = amount.length() % delimiterDigit;
		if (firstTokenLength > 0) {
			formattedAmount.append(amount.substring(0, firstTokenLength));
		}
		for (int i = firstTokenLength; i < amount.length(); i += delimiterDigit) {
			if (i > 0) {
				formattedAmount.append(",");
			}
			formattedAmount.append(amount.substring(i, i + delimiterDigit));
		}
		// System.out.println(formattedAmount);
		return formattedAmount.toString();
	}

}