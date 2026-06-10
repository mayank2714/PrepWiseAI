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
        throw err;
    }
}

export const login = async(loginData) => {
    try{
        const response = await api.post('/api/auth/login', loginData)
        return response.data   
    }
    catch(err)
    {
        throw err;  
    }
}

export const logout = async() => {
    try{
        const response = await api.get('/api/auth/logout')
        return response.data
    }
    catch(err)
    {
        throw err;
    }   
}

export const getMyProfile = async () =>{
    try{
        const response = await api.get('/api/auth/getProfile')
        return response.data
    }
    catch(err)
    {
        throw err;
    }
}

export const verifyOtpAndRegisterUser = async(registerData) => {
    try{
        const response = await api.post('/api/auth/register', registerData);
        return response.data;
    }
    catch(err){
        throw err;
    }
}