"use client";
import { fetchApi } from "@/lib/client";
import { PostDto } from "@/type/post";
import { useParams, useRouter } from "next/navigation";
import { useEffect, useState } from "react";

export default function Edit() {

    const [post, setPost] = useState<PostDto | null>(null);
    const router = useRouter();
    const { id } = useParams();

    useEffect(() => {
        fetchApi(`/api/v1/posts/${id}`)
            .then(setPost);
    }, []);

    const onSubmitHandler = (e: any) => {
        e.preventDefault();;
        const form = e.target;
        const title = form.title;
        const content = form.content;

        if (title.value.length === 0) {
            alert("제목을 입력해주세요.");
            title.focus();
            return;
        }

        if (title.value.length >= 10 || title.value.length < 2) {
            alert("2글자 이상 10자 미만으로 작성해주세요");
            title.focus();
            return;
        }

        if (content.value.length === 0) {
            alert("내용을 입력해주세요.");
            content.focus();
            return;
        }

        if (content.value.length >= 100 || content.value.length < 2) {
            alert("2글자 이상 100자 미만으로 작성해주세요");
            content.focus();
            return;
        }

        // db에 수정
        fetchApi(`/api/v1/posts/${id}`, {
            method: "PUT",
            body: JSON.stringify({
                "title": title.value,
                "content": content.value
            })
        })
            .then(rs => {
                router.replace(`/posts/${id}`)
            })
    }

    if (post == null) return <div>로딩중..</div>

    return (
        <>
            <h1>글 수정</h1>

            <form onSubmit={onSubmitHandler} className="flex flex-col gap-4">
                <input
                    type="text"
                    name="title"
                    className="border-1 rounded p-2"
                    placeholder="제목을 입력해주세요"
                    defaultValue={post.title} />
                <textarea
                    rows={10}
                    name="content"
                    className="border-1 rounded p-2"
                    placeholder="내용을 입력해주세요"
                    defaultValue={post.content}></textarea>
                <button
                    className="bg-blue-500 text-white p-2 rounded"
                    type="submit">
                    저장
                </button>
            </form>
        </>
    )
}