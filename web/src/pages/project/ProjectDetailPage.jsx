import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axiosInstance from '../../api/axiosInstance';
import ProjectTypeSelector from '../../components/project/create/ProjectTypeSelector';
import TermsInputGroup from '../../components/project/create/TermsInputGroup';
import DataCollectionSection from '../../components/project/create/DataCollectionSection';
import AirMetaDataList from '../../components/project/create/AirMetaDataList';

function ProjectDetailPage() {
  const { projectId } = useParams();
  const [projectData, setProjectData] = useState(null);

  useEffect(() => {
    const fetchProject = async () => {
      try {
        const response = await axiosInstance.get(`/pm/projects/${projectId}`);
        setProjectData(response.data.data);
      } catch (error) {
        console.error('프로젝트 데이터를 불러오는 데 실패했습니다:', error);
      }
    };

    fetchProject();
  }, [projectId]);

  if (!projectData) {
    return <div>로딩 중...</div>;
  }

  const showHealth = projectData?.projectType === 'HEALTH_DATA' || projectData?.projectType === 'BOTH';
  const showAir = projectData?.projectType === 'AIR_QUALITY' || projectData?.projectType === 'BOTH';

  return (
    <div className="bg-neutral-200 min-h-screen p-8 text-gray-800">
      <div className="mx-auto min-w-[890px] max-w-3xl bg-white shadow-lg rounded-2xl p-10 space-y-10 border border-gray-200">
        <header className="text-center mb-8">
          <h1 className="text-4xl font-extrabold text-indigo-800 mb-2 tracking-tight">Project Details</h1>
          <p className="text-lg text-gray-500">프로젝트의 상세 정보를 확인하세요.</p>
        </header>

        <section>
          <h2 className="text-2xl font-bold mb-4 text-indigo-700 border-b pb-2 border-indigo-100">Project Type</h2>
          <ProjectTypeSelector projectType={projectData.projectType} readOnly />
        </section>

        <section>
          <h2 className="text-2xl font-bold mb-4 text-indigo-700 border-b pb-2 border-indigo-100">Project Information</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block font-semibold mb-1 text-gray-700">Title</label>
              <input
                type="text"
                className="w-full border-2 rounded-lg px-4 py-2 border-indigo-200 bg-gray-100"
                value={projectData.projectTitle}
                readOnly
              />
            </div>
            <div>
              <label className="block font-semibold mb-1 text-gray-700">Participants</label>
              <input
                type="number"
                className="w-full border-2 rounded-lg px-4 py-2 border-indigo-200 bg-gray-100"
                value={projectData.participant}
                readOnly
              />
            </div>
            <div>
              <label className="block font-semibold mb-1 text-gray-700">Start Date</label>
              <input
                type="date"
                className="w-full border-2 rounded-lg px-4 py-2 border-indigo-200 bg-gray-100"
                value={projectData.startDate}
                readOnly
              />
            </div>
            <div>
              <label className="block font-semibold mb-1 text-gray-700">End Date</label>
              <input
                type="date"
                className="w-full border-2 rounded-lg px-4 py-2 border-indigo-200 bg-gray-100"
                value={projectData.endDate}
                readOnly
              />
            </div>
            <div className="md:col-span-2">
              <label className="block font-semibold mb-1 text-gray-700">Description</label>
              <textarea
                className="w-full border-2 rounded-lg px-4 py-2 border-indigo-200 bg-gray-100 min-h-[70px]"
                value={projectData.description}
                readOnly
              ></textarea>
            </div>
          </div>
        </section>

        <section>
          <h2 className="text-2xl font-bold mb-4 text-indigo-700 border-b pb-2 border-indigo-100">Agreements</h2>
          <TermsInputGroup
            termsOfPolicy={projectData.termsOfPolicy}
            privacyPolicy={projectData.privacyPolicy}
            healthDataConsent={projectData.healthDataConsent}
            airDataConsent={projectData.airDataConsent}
            localDataTermsOfService={projectData.localDataTermsOfService}
            readOnly
          />
        </section>

        <section>
          <h2 className="text-2xl font-bold mb-4 text-indigo-700 border-b pb-2 border-indigo-100">Data Collection</h2>
          <DataCollectionSection
            personalInfo={{
              email: projectData.email,
              gender: projectData.gender,
              nationalCode: projectData.nationalCode,
              phoneNumber: projectData.phoneNumber,
              dateOfBirth: projectData.dateOfBirth,
              bloodType: projectData.bloodType,
              weight: projectData.weight,
              height: projectData.height,
              name: projectData.name,
            }}
            healthData={{
              stepCount: projectData.stepCount,
              runningSpeed: projectData.runningSpeed,
              basalEnergyBurned: projectData.basalEnergyBurned,
              activeEnergyBurned: projectData.activeEnergyBurned,
              sleepAnalysis: projectData.sleepAnalysis,
              heartRate: projectData.heartRate,
              oxygenSaturation: projectData.oxygenSaturation,
              bloodPressureSystolic: projectData.bloodPressureSystolic,
              bloodPressureDiastolic: projectData.bloodPressureDiastolic,
              respiratoryRate: projectData.respiratoryRate,
              bodyTemperature: projectData.bodyTemperature,
              ecgData: projectData.ecgData,
              watchDeviceLatitude: projectData.watchDeviceLatitude,
              watchDeviceLongitude: projectData.watchDeviceLongitude,
            }}
            airData={{
              pm25Value: projectData.pm25Value,
              pm25Level: projectData.pm25Level,
              pm10Value: projectData.pm10Value,
              pm10Level: projectData.pm10Level,
              temperature: projectData.temperature,
              temperatureLevel: projectData.temperatureLevel,
              humidity: projectData.humidity,
              humidityLevel: projectData.humidityLevel,
              co2Value: projectData.co2Value,
              co2Level: projectData.co2Level,
              vocValue: projectData.vocValue,
              vocLevel: projectData.vocLevel,
              picoDeviceLatitude: projectData.picoDeviceLatitude,
              picoDeviceLongitude: projectData.picoDeviceLongitude,
            }}
            showHealth={showHealth}
            showAir={showAir}
            readOnly
          />
        </section>

        {projectData.airMetaDataItemList && projectData.airMetaDataItemList.length > 0 && (
          <section>
            <h2 className="text-2xl font-bold mb-4 text-indigo-700 border-b pb-2 border-indigo-100">Air Metadata</h2>
            <AirMetaDataList airMetaDataItems={projectData.airMetaDataItemList} readOnly />
          </section>
        )}
      </div>
    </div>
  );
}

export default ProjectDetailPage;
