import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import axios from "axios";

const initialState = {
  profileId: null,
  postList: null,
 
};
async function deletePost(postIds) {
  console.log("in delete");
    const response = await axios({
      method: "delete",
      url: "https://tweetappfse.azurewebsites.net/api/v1.0/tweets/"+localStorage.getItem("loginId")+"/delete/"+postIds,
      headers: {
        Authorization: "Bearer "+localStorage.getItem("token"),
      },
    });
    console.log("after delete");
    return response.data;
}

export const getProfilePosts = createAsyncThunk(
  "https://tweetappfse.azurewebsites.net/api/v1.0/tweets/"+localStorage.getItem("loginId"),
  async (thunkAPI) => {
  //  alert(localStorage.getItem("loginId"));
    const response = await axios({
      method: "get",
      url: "https://tweetappfse.azurewebsites.net/api/v1.0/tweets/"+localStorage.getItem("loginId"),
      headers: {
        Authorization: "Bearer "+localStorage.getItem("token"),
        "Content-Type": "application/json",
        // "Access-Control-Allow-Origin": "*",
        // "Access-Control-Allow-Methods":"PUT, POST, GET, DELETE, PATCH, OPTIONS" 
      },
    });
    console.log(response);
    return response.data;
 
  }
);



export const checkProfileSlice = createSlice({
  name: "checkProfileSlice",
  initialState,
  reducers: {
    getProfileId: (state, action) => {
      state.profileId = action.payload;
    },
    deleteTweet: (state, action) => {
        state.postList=state.postList.filter(item => item.id !== action.payload.postId);
        deletePost(action.payload.postId);
    }
  },
  extraReducers: (builder) => {
    builder.addCase(getProfilePosts.fulfilled, (state, action) => {
      state.postList = action.payload;
    });
    
  },
});

export const { getProfileId ,deleteTweet} = checkProfileSlice.actions;
export default checkProfileSlice.reducer;
