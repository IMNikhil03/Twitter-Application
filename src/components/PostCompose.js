import React, { useState } from "react";
import { Button, Form } from "react-bootstrap";

import axios from "axios";

import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

import { Hashicon } from "@emeraldpay/hashicon-react";
import { useDispatch, useSelector } from "react-redux";
import {getFollowingPosts} from "../feature/Post/PostSlice";

function PostCompose() {
  const dispatch = useDispatch();
  
  const storeFollowingPosts = useSelector((state) => state.PostReducer.followingPosts);

  const [userFullname, setUserFullname] = useState(
    localStorage.getItem("loginId")
  );
  const [postContent, setPostContent] = useState("");
  const [postContentCount, setPostContentCount] = useState(0);
  const [disablePostButton, setDisablePostButton] = useState(true);
  
  
  
  function showSuccessMessage(inputMessage) {
    toast.success(inputMessage, {
      position: "bottom-center",
      autoClose: 3000,
      hideProgressBar: false,
      closeOnClick: true,
      pauseOnHover: true,
      draggable: true,
      progress: undefined,
      theme: "colored",
    });
  }

  function showFailMessage(inputMessage) {
    toast.error(inputMessage, {
      position: "bottom-center",
      autoClose: 3000,
      hideProgressBar: false,
      closeOnClick: true,
      pauseOnHover: true,
      draggable: true,
      progress: undefined,
      theme: "colored",
    });
  }

  function handleContentChange(e) {
    setPostContent(e.target.value);
    setPostContentCount(e.target.value.length);
    if (postContentCount <2 || postContentCount > 144) {
      setDisablePostButton(true);
    } else {
      setDisablePostButton(false);
    }
  }

  async function createPost(inputContent) {
    try {
      const response = await axios({
        method: "post",
        url: "http://localhost:8080/api/v1.0/tweets/"+localStorage.getItem("loginId")+"/add",
        headers: {
          Authorization: "Bearer "+localStorage.getItem("token"),
        },
        data: {
          message: inputContent
        },
      });
      console.log(response);
      if (response.data !== null && response.status === 201) {
        showSuccessMessage("Posted successfully!");
        setPostContent("");
        setPostContentCount(0);
        setDisablePostButton(true);
      }

      if (response.data !== null && response.status !== 201) {
        showFailMessage("Post failed. Please try again later!");
      }
    } catch (error) {
      showFailMessage("Post failed. Please try again later!");
    }
  }

  async function handleCreatePost(e) {
    e.preventDefault();
    createPost(postContent);
    dispatch(getFollowingPosts());
  }

  return (
    <div>
      {/* <h1>PostCompose component</h1> */}
      <div className="border rounded-3 border-success p-3 shadow">
        <ToastContainer />
        <Form className="d-flex flex-column">
          <Form.Group className="mb-3">
            <Form.Label>
              <div className="d-flex align-items-center mb-1">
                <div className="mx-3">
                  <Hashicon value={localStorage.getItem("loginId")} size={60} />
                </div>
                <div className="fs-4 fw-bold">{userFullname}</div>
              </div>
            </Form.Label>
            <Form.Control
              as="textarea"
              row={4}
              placeholder="What is happening?"
              value={postContent}
              onChange={handleContentChange}
              style={{ resize: "none", height: "7rem" }}
            />
          </Form.Group>
          <div className="d-flex justify-content-end align-items-center">
            <span>Characters: {postContentCount}/144</span>
            <Button
              onClick={handleCreatePost}
              variant="success"
              disabled={disablePostButton}
              className="col-2 mx-3"
            >
              Post
            </Button>
          </div>
        </Form>
      </div>
    </div>
  );
}

export default PostCompose;
