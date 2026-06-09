import axios from 'axios'

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    withCredentials: true
})

export const sendOtp = async (otpData) => {

    try{
        const response = await api.post('/api/verify/send-otp', otpData)
        return response.data
    } catch (error) {
        console.log(error)
        throw new Error(error.response?.data?.message || 'Registration failed')
    }
}

export const login = async(loginData) => {
    try{
        const response = await api.post('/api/auth/login', loginData)
        return response.data   
    }
    catch(err)
    {
        console.log('received error');
        throw new Error(err.response?.data?.message || 'Login failed')
    }
}

export const logout = async() => {
    try{
        const response = await api.get('/api/auth/logout')
        return response.data
    }
    catch(err)
    {
        console.log(err);
        throw new Error(err.response?.data?.message || 'Logout failed')
    }   
}

export const getMyProfile = async () =>{
    try{
        const response = await api.get('/api/auth/getProfile')
        return response.data
    }
    catch(err)
    {
        console.log(err);
        throw new Error(err.response?.data?.message || 'Failed to fetch profile')
    }
}

export const verifyOtpAndRegisterUser = async(registerData) => {
    try{
        const response = await api.post('/api/auth/register', registerData);
        return response.data;
    }
    catch(err){
        console.log(err);
        throw new Error(err.response?.data?.message || 'Failed to verify OTP')
    }
}