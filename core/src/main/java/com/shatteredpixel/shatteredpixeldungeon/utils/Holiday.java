/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.utils;

import com.nlf.calendar.Lunar;
import com.nlf.calendar.Solar;

import java.util.Calendar;
import java.util.GregorianCalendar;

public enum Holiday {

	NONE,

	LUNAR_NEW_YEAR,         //Varies, sometime in late Jan to Late Feb              (7 days)
	FLOWER_FESTIVAL,        //Varies, sometime in early Mar to early Apr              (7 days)
	APRIL_FOOLS,            //April 1st, can override easter                        (1 day)
	EASTER,                 //Varies, sometime in Late Mar to Late Apr              (6-7 days)
	XUNS_BIRTHDAY,          //Apr 15th to 21st                                      (7 days)
	//5月无事发生
	DRAGON_BOAT,            //Varies, sometime in late May to late Jun              (7 days)
	//7月无事发生
	SHATTEREDPD_BIRTHDAY,   //Aug 1st to Aug 7th                                    (7 days)
	MID_AUTUMN,             //Varies, sometime in early Sep to early Oct            (7 days)
	HALLOWEEN,              //Oct 24th to Oct 31st                                  (7 days)
	//11月无事发生
	PD_BIRTHDAY,            //Dec 1st to Dec 7th                                    (7 days)
	WINTER_HOLIDAYS,        //Dec 15th to Dec 26th                                  (12 days)
	NEW_YEARS;              //Dec 27th to Jan 2nd                                   (7 days)

	//total of 85-86 festive days each year, mainly concentrated in Late Oct to Early Feb

	//we cache the holiday here so that holiday logic doesn't suddenly shut off mid-game
	//this gets cleared on game launch (of course), and whenever leaving a game scene
	private static Holiday cached;
	public static void clearCachedHoliday(){
		cached = null;
	}

	public static Holiday getCurrentHoliday(){
		if (cached == null){
			cached = getHolidayForDate((GregorianCalendar) GregorianCalendar.getInstance());
		}
		return cached;
	}

	//requires a gregorian calendar
	public static Holiday getHolidayForDate(GregorianCalendar cal){

		//Lunar New Year
		if (isLunarNewYear(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE))){
			return LUNAR_NEW_YEAR;
		}

		//April Fools (priotized) and Flower Festival
		if (cal.get(Calendar.MONTH) == Calendar.APRIL
				&& cal.get(Calendar.DAY_OF_MONTH) == 1){
			return APRIL_FOOLS;
		}

		if (isFlowerFestival(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE))){
			return FLOWER_FESTIVAL;
		}

		//X_U_NPD's Birthday (priotized) and Easter
		if (cal.get(Calendar.MONTH) == Calendar.APRIL
				&& cal.get(Calendar.DAY_OF_MONTH) >= 15
				&& cal.get(Calendar.DAY_OF_MONTH) <= 21){
			return XUNS_BIRTHDAY;
		}

		if (isEaster(cal.get(Calendar.YEAR),
				cal.get(Calendar.DAY_OF_YEAR),
				cal.getActualMaximum(Calendar.DAY_OF_YEAR) == 366)){
			return EASTER;
		}

		//DragonBoat
		if (isDragonBoat(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE))){
			return DRAGON_BOAT;
		}

		//Shattered's Birthday
		if (cal.get(Calendar.MONTH) == Calendar.AUGUST
				&& cal.get(Calendar.DAY_OF_MONTH) <= 7){
			return SHATTEREDPD_BIRTHDAY;
		}

		//Mid-Autumn
		if (isMidAutumn(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE))){
			return MID_AUTUMN;
		}

		//Halloween
		if (cal.get(Calendar.MONTH) == Calendar.OCTOBER
				&& cal.get(Calendar.DAY_OF_MONTH) >= 24){
			return HALLOWEEN;
		}

		//Pixel Dungeon's Birthday
		if (cal.get(Calendar.MONTH) == Calendar.DECEMBER
				&& cal.get(Calendar.DAY_OF_MONTH) <= 7){
			return PD_BIRTHDAY;
		}

		//Winter Holidays
		if (cal.get(Calendar.MONTH) == Calendar.DECEMBER
				&& cal.get(Calendar.DAY_OF_MONTH) >= 15
				&& cal.get(Calendar.DAY_OF_MONTH) <= 26){
			return WINTER_HOLIDAYS;
		}

		//New Years
		if ((cal.get(Calendar.MONTH) == Calendar.DECEMBER && cal.get(Calendar.DAY_OF_MONTH) >= 27)
				|| (cal.get(Calendar.MONTH) == Calendar.JANUARY && cal.get(Calendar.DAY_OF_MONTH) <= 2)){
			return NEW_YEARS;
		}

		return NONE;
	}

	//has to be hard-coded on a per-year basis =S
	//by using a dependency, we solved the hard-code problem!
	public static boolean isLunarNewYear(int year, int month, int day){

		Lunar lunarDate = new Lunar(new Solar(year, month+1, day));

		for (int i = -4; i < 3; i++){
			if (lunarDate.next(i).getFestivals().contains("春节")){
				return true;
			}
		}
		return false;
	}

	public static boolean isFlowerFestival(int year, int month, int day){

		Lunar lunarDate = new Lunar(new Solar(year, month+1, day));
		return lunarDate.getMonth() == 2 && lunarDate.getDay() >= 11 && lunarDate.getDay() <= 17;
	}

	public static boolean isMidAutumn(int year, int month, int day){

		Lunar lunarDate = new Lunar(new Solar(year, month+1, day));
		return lunarDate.getMonth() == 8 && lunarDate.getDay() >= 11 && lunarDate.getDay() <= 17;
	}
	
	public static boolean isDragonBoat(int year, int month, int day){

		Lunar lunarDate = new Lunar(new Solar(year, month+1, day));
		return lunarDate.getMonth() == 5 && lunarDate.getDay() >= 1 && lunarDate.getDay() <= 7;
	}

	//has to be algorithmically computed =S
	public static boolean isEaster(int year, int dayOfYear, boolean isLeapYear){
		//if we're not in March or April, just skip out of all these calculations
		if (dayOfYear < 59 || dayOfYear > 121) {
			return false;
		}

		//Uses the Anonymous Gregorian Algorithm
		int a = year % 19;
		int b = year / 100;
		int c = year % 100;
		int d = b / 4;
		int e = b % 4;
		int f = (b + 8) / 25;
		int g = (b - f + 1) / 3;
		int h = (a*19 + b - d - g + 15) % 30;
		int i = c / 4;
		int k = c % 4;
		int l = (32 + 2*e + 2*i - h - k) % 7;
		int m = (a + h*11 + l*22)/451;
		int p = h + l - m * 7 + 114;
		int n = p / 31;
		int o = p % 31;

		int easterDayOfYear = 0;

		if (n == 3){
			easterDayOfYear += 59; //march
		} else {
			easterDayOfYear += 90; //april
		}

		if (isLeapYear) {
			easterDayOfYear += 1; //add an extra day to account for February 29th
		}

		easterDayOfYear += (o+1); //add day of month

		//celebrate for 7 days total, with Easter Sunday on the 5th day
		return dayOfYear >= easterDayOfYear-4 && dayOfYear <= easterDayOfYear+2;
	}

}
