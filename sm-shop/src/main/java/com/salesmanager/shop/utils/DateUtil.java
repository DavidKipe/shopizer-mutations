/*
 * Licensed to csti consulting
 * You may obtain a copy of the License at
 *
 * http://www.csticonsulting.com
 * Copyright (c) 2006-Aug 24, 2010 Consultation CS-TI inc.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.salesmanager.shop.utils;

import com.salesmanager.core.business.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateUtil {

	private Date startDate = new Date(new Date().getTime());
	private Date endDate = new Date(new Date().getTime());
	private static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);
	private final static String LONGDATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss Z";


	/**
	 * Generates a time stamp
	 * yyyymmddhhmmss
	 *
	 * @return
	 */
	public static String generateTimeStamp() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmSS");
		System.out.println("$#15602#");
		return format.format(new Date());
	}

	/**
	 * yyyy-MM-dd
	 *
	 * @param dt
	 * @return
	 */
	public static String formatDate(Date dt) {

		System.out.println("$#15603#");
		if (dt == null) {
			System.out.println("$#15604#");
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT);
		System.out.println("$#15605#");
		return format.format(dt);

	}

	public static String formatYear(Date dt) {

		System.out.println("$#15606#");
		if (dt == null) {
			System.out.println("$#15607#");
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT_YEAR);
		System.out.println("$#15608#");
		return format.format(dt);

	}

	public static String formatLongDate(Date date) {

		System.out.println("$#15609#");
		if (date == null) {
			System.out.println("$#15610#");
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat(LONGDATE_FORMAT);
		System.out.println("$#15611#");
		return format.format(date);

	}

	/**
	 * yy-MMM-dd
	 *
	 * @param dt
	 * @return
	 */
	public static String formatDateMonthString(Date dt) {

		System.out.println("$#15612#");
		if (dt == null) {
			System.out.println("$#15613#");
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT);
		System.out.println("$#15614#");
		return format.format(dt);

	}

	public static Date getDate(String date) throws Exception {
		DateFormat myDateFormat = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT);
		System.out.println("$#15615#");
		return myDateFormat.parse(date);
	}

	public static Date addDaysToCurrentDate(int days) {
		Calendar c = Calendar.getInstance();
		System.out.println("$#15616#");
		c.setTime(new Date());
		System.out.println("$#15617#");
		c.add(Calendar.DATE, days);
		System.out.println("$#15618#");
		return c.getTime();

	}

	public static Date getDate() {

		System.out.println("$#15619#");
		return new Date(new Date().getTime());

	}

	public static String getPresentDate() {

		Date dt = new Date();

		SimpleDateFormat format = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT);
		System.out.println("$#15620#");
		return format.format(new Date(dt.getTime()));
	}

	public static String getPresentYear() {

		Date dt = new Date();

		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		System.out.println("$#15621#");
		return format.format(new Date(dt.getTime()));
	}

	public static boolean dateBeforeEqualsDate(Date firstDate, Date compareDate) {


		System.out.println("$#15622#");
		if (firstDate == null || compareDate == null) {
			System.out.println("$#15624#");
			return true;
		}

		System.out.println("$#15626#");
		System.out.println("$#15625#");
		if (firstDate.compareTo(compareDate) > 0) {
			System.out.println("$#15627#");
			return false;
		} else if (firstDate.compareTo(compareDate) < 0) {
			System.out.println("$#15628#");
			System.out.println("$#15629#");
			System.out.println("$#15630#");
			return true;
		} else if (firstDate.compareTo(compareDate) == 0) {
			System.out.println("$#15631#");
			System.out.println("$#15632#");
			return true;
		} else {
			System.out.println("$#15633#");
			return false;
		}

	}

	public void processPostedDates(HttpServletRequest request) {
		Date dt = new Date();
		DateFormat myDateFormat = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT);
		Date sDate = null;
		Date eDate = null;
		try {
			System.out.println("$#15634#");
			if (request.getParameter("startdate") != null) {
				sDate = myDateFormat.parse(request.getParameter("startdate"));
			}
			System.out.println("$#15635#");
			if (request.getParameter("enddate") != null) {
				eDate = myDateFormat.parse(request.getParameter("enddate"));
			}
			this.startDate = sDate;
			this.endDate = eDate;
		} catch (Exception e) {
			LOGGER.error("", e);
			this.startDate = new Date(dt.getTime());
			this.endDate = new Date(dt.getTime());
		}
	}

	public Date getEndDate() {
		System.out.println("$#15636#");
		return endDate;
	}

	public Date getStartDate() {
		System.out.println("$#15637#");
		return startDate;
	}

}
