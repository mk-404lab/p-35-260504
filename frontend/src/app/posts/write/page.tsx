"use client";

import { fetchApi } from "@/lib/client";
import { useRouter } from "next/navigation";

export default function Write() {

    const router = useRouter();

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

        // db에 저장.
        fetchApi(`/api/v1/posts`, {
            method: "POST",
            body: JSON.stringify({
                "title": title.value,
                "content": content.value
            })
        })
            .then(rs => {
                alert("글이 정상적으로 작성되었습니다.");
                // 글 상세 페이지로 이동
                router.replace(`/posts/${rs.data.postDto.id}`)
            })
    }

    return (
        <>
            <h1>글 작성</h1>

            <form action="" onSubmit={onSubmitHandler} className="flex flex-col gap-4">
                <input type="text" name="title" className="border-1 rounded p-2" placeholder="제목을 입력해주세요" />
                <textarea rows={10} name="content" className="border-1 rounded p-2" placeholder="내용을 입력해주세요"></textarea>
                <button className="bg-blue-500 text-white p-2 rounded" type="submit">
                    저장
                </button>
            </form>
        </>
    )
}