const gameId = document.getElementById('gameId').value //hidden input
const commentForm = document.getElementById('commentForm')


async function attachEvents() {
    document.getElementById("comments-btn").addEventListener("click", displayPostsSelection);
   // document.getElementById("btnViewPost").addEventListener("click", displayContent)
}
attachEvents();
async function displayPostsSelection() {
    const posts = await getPosts();//DON'T FORGET THE await!!!!!
    const selectPosts = document.getElementById("posts"); //drop-down (<select>)
    selectPosts.replaceChildren(); //prevent duplicating posts
    for (postId in posts) {
        const option = document.createElement("option");
        option.value = postId;//used to get the post url in viewPost
        option.textContent = posts[postId].title;
        selectPosts.appendChild(option);
    }
}

async function displayContent() {
    const postTitleH1 = document.getElementById("post-title");
    const postBodyP = document.getElementById("post-body");
    const postCommentsUl = document.getElementById("post-comments");
    const post_id = document.getElementById("posts").value; //currently selected post from the drop-down (select)
    //Duplicate DOM search, again in displayPostsSelection...
    postCommentsUl.replaceChildren();//Clear comments
    postBodyP.textContent = "Loading..."
    const [post, comments] = await Promise.all([getPost(post_id), getComments()]);
    for (commentId in comments) {
        if (comments[commentId].postId == post_id) {//find the comments for the current post by postId
            //display comment as <li> in postBodyUl
            const commentLi = document.createElement("li");
            commentLi.textContent = comments[commentId].text;
            postCommentsUl.appendChild(commentLi);
            //Or filter: const comments = Object.values(comments).filter(c => c.postId == post_id);
        }
    }
    postTitleH1.textContent = post.title;
    postBodyP.textContent = post.body;
}

async function getPosts() {
    try {
        const postsResponse = await fetch("http://localhost:3030/jsonstore/blog/posts");
        if (postsResponse.status != 200) {
            throw new Error("Cannot retrieve posts.")
        }
        return await postsResponse.json();
    } catch (error) {
        document.getElementById("post-title").textContent = "Error";
    }
}
async function getPost(post_id) {
    try {
        const postResponse = await fetch("http://localhost:3030/jsonstore/blog/posts/" + post_id);
        if (postResponse.status != 200) {
            throw new Error("Post not found.")
        }
        return await postResponse.json();
    } catch (error) {
        document.getElementById("post-title").textContent = "Error";
    }
}
async function getComments() {
    try {
        const commentsResponse = await fetch("http://localhost:3030/jsonstore/blog/comments");
        if (commentsResponse.status != 200) {
            throw new Error("Comments not found.")
        }
        return await commentsResponse.json();
    } catch (error) {
        document.getElementById("post-title").textContent = "Error";
    }
}
