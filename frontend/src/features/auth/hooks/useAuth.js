import { useContext, useEffect } from "react";
import { AuthContext } from "../context/authContext";
import { login, register, logout, getMyProfile } from "../services/authService";



export const useAuth = () => {

    const context = useContext(AuthContext)
    const { user, setUser, loading, setLoading } = context


    const handleLogin = async ({ email, password }) => {
        setLoading(true)
        try {
            const data = await login({ email, password })
            setUser(data.user)
        } catch (err) {
            throw new Error('Login failed')
        } finally {
            setLoading(false)
        }
    }

    const handleRegister = async ({ username, email, password }) => {
        setLoading(true)
        try {
            const data = await register({ username, email, password })
            setUser(data.user)
        } catch (err) {
            throw new Error('Registration failed')
        } finally {
            setLoading(false)
        }
    }

    const handleLogout = async () => {
        setLoading(true)
        try {
            const data = await logout()
            setUser(null)
        } catch (err) {

        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        const getAndSetUser = async () => {
            try {
                const data = await getMyProfile()
                setUser(data)
            } catch (err) {
                console.log(err);
             } finally {
                setLoading(false)
            }
        }

        getAndSetUser()

    }, [])
    

    return { user, loading, handleRegister, handleLogin, handleLogout }
}