import { fetchApi } from "@/lib/client";
import { createContext, useState } from "react";


const AuthContext = createContext<ReturnType<typeof useAuth> | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
    const authState = useAuth();

    return (
        <AuthContext.Provider value={authState}>{children}</AuthContext.Provider>
    );
}

export function useAuth() {

    const [loginMember, setLoginMember] = useState<MemberDto | null>(null);

    const getLoginMember = () => {
        fetchApi("/api/v1/members/me")
            .then((memberDto) => {
                setLoginMember(memberDto);
            })
            .catch((err) => {
            });
    }

    const logout = () => {
        confirm("로그아웃 하시겠습니까?") &&
            fetchApi("/api/v1/members/logout", {
                method: "DELETE",
            })
                .then((data) => {
                    setLoginMember(null);
                    alert(data.msg);
                })
                .catch((rsData) => {
                    alert(rsData.msg);
                });
    };

    return { loginMember, getLoginMember, logout };
}

