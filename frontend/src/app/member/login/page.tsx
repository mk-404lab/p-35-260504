"use client";

import { fetchApi } from "@/lib/client";
import { useRouter } from "next/navigation";

export default function Login() {

    const router = useRouter();

    const onSubmitHandler = (e: any) => {
        e.preventDefault();;
        const form = e.target;
        const username = form.username;
        const password = form.password;

        if (username.value.length === 0) {
            alert("아이디를 입력해주세요.");
            username.focus();
            return;
        }

        if (password.value.length === 0) {
            alert("비밀번호를 입력해주세요.");
            password.focus();
            return;
        }

        // db에 저장.
        fetchApi(`/api/v1/members/login`, {
            method: "POST",
            body: JSON.stringify({
                "username": username.value,
                "password": password.value
            })
        })
            .then(rs => {
                alert(rs.msg);
                router.replace(`/`)
            })
            .catch((errMsg) => {
                alert(errMsg);
            })
    }

    return (
        <>
            <h1>로그인</h1>

            <form action="" onSubmit={onSubmitHandler} className="flex flex-col gap-4">
                <input type="text" name="username" className="border-1 rounded p-2" placeholder="아이디를 입력해주세요" />
                <input type="password" name="password" className="border-1 rounded p-2" placeholder="비밀번호를 입력해주세요." />
                <button className="bg-blue-500 text-white p-2 rounded" type="submit">
                    로그인
                </button>
            </form>
        </>
    )
}