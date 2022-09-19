import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import axios from "axios";

const initialState = {
  followingAccounts: null,
  followerAccounts: null,
};





export const getAllAccounts = createAsyncThunk(
  "https://tweetappfse.azurewebsites.net/api/v1.0/tweets/users/all",
  async (thunkAPI) => {
    const response = await axios({
      method: "get",
      url: "https://tweetappfse.azurewebsites.net/api/v1.0/tweets/users/all",
      headers: {
        "Content-Type": "application/json",
      },
    });
    return response.data;
  }
);
export const getAccountsByUser = createAsyncThunk(
  "https://tweetappfse.azurewebsites.net/api/v1.0/tweets/users/search",
  async ({loginId},thunkAPI) => {
    const response = await axios({
      method: "get",
      url: "https://tweetappfse.azurewebsites.net/api/v1.0/tweets/users/search/"+loginId,
      headers: {
        "Content-Type": "application/json",
        // "Access-Control-Allow-Origin": "*",
      },
    });
    return response.data;
  }
);


export const AccountSlice = createSlice({
  name: "AccountSlice",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder.addCase(getAllAccounts.fulfilled, (state, action) => {
      state.followerAccounts = action.payload;
    });
    builder.addCase(getAccountsByUser.fulfilled, (state, action) => {
      state.followerAccounts = action.payload.response;
    });
  },
});

export default AccountSlice.reducer;
