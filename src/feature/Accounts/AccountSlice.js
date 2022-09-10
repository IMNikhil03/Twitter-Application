import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import axios from "axios";

const initialState = {
  followingAccounts: null,
  followerAccounts: null,
};





export const getAllAccounts = createAsyncThunk(
  "http://localhost:8080/api/v1.0/tweets/users/all",
  async (thunkAPI) => {
    const response = await axios({
      method: "get",
      url: "http://localhost:8080/api/v1.0/tweets/users/all",
      headers: {
        "Content-Type": "application/json",
      },
    });
    return response.data;
  }
);
export const getAccountsByUser = createAsyncThunk(
  "http://localhost:8080/api/v1.0/tweets/users/search",
  async ({loginId},thunkAPI) => {
    const response = await axios({
      method: "get",
      url: "http://localhost:8080/api/v1.0/tweets/users/search/"+loginId,
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
