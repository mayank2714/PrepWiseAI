import { getAllInterviewReports, generateInterviewReport, getInterviewReportById, generateResumePdf } from "../services/reportService"
import { useContext, useEffect } from "react"
import { ReportContext } from "../context/reportContext"
import { useParams } from "react-router"


export const useReports = () => {

    const context = useContext(ReportContext)
    const { reportId } = useParams()

    if (!context) {
        throw new Error("useReports must be used within a ReportProvider")
    }

    const { loading, setLoading, report, setReport, reports, setReports } = context

    const generateReport = async ({ jobDescription, selfDescription, resumeFile }) => {
        setLoading(true)
        let response = null
        try {
            response = await generateInterviewReport({ jobDescription, selfDescription, resumeFile })
            setReport(response)
        } catch (error) {
            console.log(error)
            throw error;
        } finally {
            setLoading(false)
        }

        return response
    }

    const getReportById = async (reportId) => {
        setLoading(true)
        let response = null
        try {
            response = await getInterviewReportById(reportId)
            setReport(response)
        } catch (error) {
            console.log(error)
            throw error;
        } finally {
            setLoading(false)
        }
        return response
    }

    const getReports = async () => {
        setLoading(true)
        let response = null
        try {
            response = await getAllInterviewReports()
            setReports(response)
        } catch (error) {
            console.log(error)
            throw error;
        } finally {
            setLoading(false)
        }

        return response
    }

    const getResumePdf = async (reportId) => {
        // setLoading(true)
        let response = null
        try {
            response = await generateResumePdf({ reportId })
            const url = window.URL.createObjectURL(new Blob([ response ], { type: "application/pdf" }))
            const link = document.createElement("a")
            link.href = url
            link.setAttribute("download", `resume_${reportId}.pdf`)
            document.body.appendChild(link)
            link.click()
        }
        catch (error) {
            console.log(error)
            throw error;
        } finally {
            // setLoading(false)
        }
    }

    useEffect(() => {
        if (reportId) {
            getReportById(reportId)
        } else {
            getReports()
        }
    }, [ reportId ])

    return { loading, report, reports, generateReport, getReportById, getReports, getResumePdf }

}