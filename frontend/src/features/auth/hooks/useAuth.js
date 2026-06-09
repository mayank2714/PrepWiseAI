import { useContext, useEffect } from "react";
import { AuthContext } from "../context/authContext";
import { login, sendOtp, logout, getMyProfile, verifyOtpAndRegisterUser } from "../services/authService";



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

    const handleSendOtp = async ( {email} ) => {
        setLoading(true)
        try {
            await sendOtp( {email} );
        } catch (err) {
            throw new Error('Failed to send OTP')
        } finally {
            setLoading(false)
        }
    }

    const handleVerifyOtpAndRegisterUser = async({ email, password, enteredOtp }) => {
        try{
            const data = await verifyOtpAndRegisterUser({ email, password, enteredOtp });
            setUser(data.user);
        }
        catch(err){
            
            throw new Error('Failed to verify OTP');
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
    

    return { user, loading, handleSendOtp, handleLogin, handleLogout, handleVerifyOtpAndRegisterUser }
}