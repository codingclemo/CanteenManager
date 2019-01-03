package com.example.canteenchecker.canteenmanager.proxy;

import com.example.canteenchecker.canteenmanager.core.Canteen;
import com.example.canteenchecker.canteenmanager.core.ReviewData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class    ServiceProxy {

	// ADMIN --> https://canteencheckeradmin.azurewebsites.net/
	// Test user: S23423432/S23423432
	// SWAGGER --> https://canteenchecker.azurewebsites.net/swagger/ui/index
	private static final String SERVICE_BASE_URL = "https://canteenchecker.azurewebsites.net/";
	private static final long ARTIFICIAL_DELAY = 100;

	private final Proxy proxy = new Retrofit.Builder()
			.baseUrl(SERVICE_BASE_URL)
			.addConverterFactory(GsonConverterFactory.create())
			.build()
			.create(Proxy.class);

	private void causeDelay() {
		try {
			Thread.sleep(ARTIFICIAL_DELAY);
		} catch (InterruptedException ignored) {
		}
	}

	public Collection<Canteen> getCanteens(String filter) throws IOException {
		causeDelay(); // for testing only
		Collection<ProxyCanteen> canteens = proxy.getCanteens(filter).execute().body();
		if (canteens == null) {
			return null;
		}
		Collection<Canteen> result = new ArrayList<>(canteens.size());
		for (ProxyCanteen canteen : canteens) {
			result.add(canteen.toCanteen());
		}
		return result;
	}

	public Canteen getCanteen(String canteenId) throws IOException {
		causeDelay(); // for testing only
		ProxyCanteen canteen = proxy.getCanteen(canteenId).execute().body();
		return canteen != null ? canteen.toCanteen() : null;
	}

	public ReviewData getReviewsDataForCanteen(String canteenId) throws IOException {
		causeDelay(); // for testing only
		ProxyReviewData reviewData = proxy.getReviewDataForCanteen(canteenId).execute().body();
		return reviewData != null ? reviewData.toReviewData() : null;
	}

	public String authenticate(String userName, String password) throws IOException {
		causeDelay(); // for testing only
		return proxy.postLogin(new ProxyLogin(userName, password)).execute().body();
	}

	public String createReview(String authToken, String canteenId, int rating, String remark) throws IOException {
		causeDelay(); // for testing only
		ProxyRating r = proxy.postRating(String.format("Bearer %s", authToken), new ProxyNewRating(canteenId, remark, rating)).execute().body();
		return r != null ? Integer.toString(r.ratingId) : null;
	}

	private interface Proxy {

		@GET("/Public/Canteen")
		Call<Collection<ProxyCanteen>> getCanteens(@Query("nameFilter") String filter);

		@GET("/Public/Canteen/{id}")
		Call<ProxyCanteen> getCanteen(@Path("id") String canteenId);

		@GET("/Public/Canteen/{id}/Rating?nrOfRatings=0")
		Call<ProxyReviewData> getReviewDataForCanteen(@Path("id") String canteenId);

		@POST("/Admin/Login")
		Call<String> postLogin(@Body ProxyLogin login);

		@POST("/Admin/Canteen/Rating")
		Call<ProxyRating> postRating(@Header("Authorization") String authenticationToken, @Body ProxyNewRating rating);

	}

	private static class ProxyCanteen {

		int canteenId;
		String name;
		String meal;
		float mealPrice;
		String website;
		String phone;
		String address;
		float averageRating;
		int averageWaitingTime;

		Canteen toCanteen() {
			return new Canteen(String.valueOf(canteenId), name, phone, website, meal, mealPrice, averageRating, address, averageWaitingTime);
		}

	}


	private static class ProxyRating {

		int ratingId;
		//String username;
		//String remark;
		//int ratingPoints;
		//long timestamp;

	}

	private static class ProxyReviewData {

		float average;
		//int count;
		int totalCount;
		//ProxyRating[] ratings;
		int[] countsPerGrade;

		private int getRatingsForGrade(int grade) {
			grade--;
			return countsPerGrade != null && grade >= 0 && grade < countsPerGrade.length ? countsPerGrade[grade] : 0;
		}

		ReviewData toReviewData() {
			return new ReviewData(average, totalCount, getRatingsForGrade(1), getRatingsForGrade(2), getRatingsForGrade(3), getRatingsForGrade(4), getRatingsForGrade(5));
		}

	}

	private static class ProxyLogin {

		final String username;
		final String password;

		ProxyLogin(String userName, String password) {
			this.username = userName;
			this.password = password;
		}

	}

	private static class ProxyNewRating {

		final int canteenId;
		final String remark;
		final int ratingPoints;

		ProxyNewRating(String canteenId, String remark, int ratingPoints) {
			this.canteenId = Integer.parseInt(canteenId);
			this.remark = remark;
			this.ratingPoints = ratingPoints;
		}

	}

}
