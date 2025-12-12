import React from 'react';
import { useNavigate } from 'react-router-dom';

import ProjectTypeSelector from '../../../components/project/create/ProjectTypeSelector';
import TermsInputGroup from '../../../components/project/create/TermsInputGroup';
import DataCollectionSection from '../../../components/project/create/DataCollectionSection';
import AirMetaDataList from '../../../components/project/create/AirMetaDataList';
import axiosInstance from '../../../api/axiosInstance';
import { useProjectCreateForm } from './useProjectCreateForm';

function ProjectCreatePage() {
  const form = useProjectCreateForm();
  const navigate = useNavigate();

  const handleRegister = async () => {
    if (!form.validateForm()) {
      alert('Please fill in all required fields.');
      return;
    }

    // 최소 1개 이상 health/air 항목 선택 여부 검사
    const isAnyHealthSelected = form.showHealth && Object.values(form.healthData).some(Boolean);
    const isAnyAirSelected = form.showAir && Object.values(form.airData).some(Boolean);
    if ((form.showHealth && !isAnyHealthSelected) || (form.showAir && !isAnyAirSelected)) {
      alert('Please select at least one item from Health or Air data section.');
      return;
    }

    const payload = {
      projectTitle: form.title,
      participant: Number(form.participant),
      description: form.description,
      projectType: form.projectType,
      termsOfPolicy: form.termsOfPolicy,
      privacyPolicy: form.privacyPolicy,
      healthDataConsent: form.healthDataConsent,
      airDataConsent: form.airDataConsent,
      localDataTermsOfService: form.localDataTermsOfService,
      startDate: form.startDate,
      endDate: form.endDate,
      createdAt: new Date().toISOString().slice(0, 19).replace('T', ' '),
      email: form.personalInfo.email,
      gender: form.personalInfo.gender,
      nationalCode: form.personalInfo.nationalCode,
      phoneNumber: form.personalInfo.phoneNumber,
      dateOfBirth: form.personalInfo.birthDate,
      bloodType: form.personalInfo.bloodType,
      weight: form.personalInfo.weight,
      height: form.personalInfo.height,
      name: form.personalInfo.name,
      stepCount: form.healthData.steps,
      runningSpeed: form.healthData.runningSpeed,
      basalEnergyBurned: form.healthData.basalEnergyBurned,
      activeEnergyBurned: form.healthData.activeEnergyBurned,
      sleepAnalysis: form.healthData.sleepAnalysis,
      heartRate: form.healthData.heartRate,
      oxygenSaturation: form.healthData.oxygenSaturation,
      bloodPressureSystolic: form.healthData.bloodPressureSystolic,
      bloodPressureDiastolic: form.healthData.bloodPressureDiastolic,
      respiratoryRate: form.healthData.respiratoryRate,
      bodyTemperature: form.healthData.bodyTemperature,
      ecgData: form.healthData.ecgData,
      watchDeviceLatitude: form.healthData.watchDeviceLatitude,
      watchDeviceLongitude: form.healthData.watchDeviceLongitude,
      pm25Value: form.airData.pm25Value,
      pm25Level: form.airData.pm25Level,
      pm10Value: form.airData.pm10Value,
      pm10Level: form.airData.pm10Level,
      temperature: form.airData.temperature,
      temperatureLevel: form.airData.temperatureLevel,
      humidity: form.airData.humidity,
      humidityLevel: form.airData.humidityLevel,
      co2Value: form.airData.co2Value,
      co2Level: form.airData.co2Level,
      vocValue: form.airData.vocValue,
      vocLevel: form.airData.vocLevel,
      picoDeviceLatitude: form.airData.picoDeviceLatitude,
      picoDeviceLongitude: form.airData.picoDeviceLongitude,
      airMetaDataItemList: form.airMetaDataItems
    };

    try {
      await axiosInstance.post('/pm/projects', payload);
      alert('Project has been successfully registered!');
      navigate('/projects');
    } catch (error) {
      alert('Failed to register the project.');
      console.error(error);
    }
  };

  return (
    <div className="bg-neutral-200 min-h-screen p-8 text-gray-800">
      <div className="mx-auto min-w-[890px] max-w-3xl bg-white shadow-lg rounded-2xl p-10 space-y-10 border border-gray-200">
        <header className="text-center mb-8">
          <h1 className="text-4xl font-extrabold text-indigo-800 mb-2 tracking-tight">Project Registration</h1>
          <p className="text-lg text-gray-500">Please fill out the form below to create a new research project.</p>
        </header>

        <section>
          <h2 className="text-2xl font-bold mb-4 text-indigo-700 border-b pb-2 border-indigo-100">Project Type</h2>
          <ProjectTypeSelector projectType={form.projectType} setProjectType={form.setProjectType} />
        </section>

        <section>
          <h2 className="text-2xl font-bold mb-4 text-indigo-700 border-b pb-2 border-indigo-100">Project Information</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block font-semibold mb-1 text-gray-700">Title</label>
              <input
                type="text"
                className={`w-full border-2 rounded-lg px-4 py-2 focus:ring-2 transition ${form.errors.title ? 'border-red-400 focus:ring-red-300' : 'border-indigo-200 focus:ring-indigo-400'}`}
                value={form.title}
                onChange={e => form.setTitle(e.target.value)}
              />
              {form.errors.title && <p className="text-sm text-red-500 mt-1">{form.errors.title}</p>}
            </div>
            <div>
              <label className="block font-semibold mb-1 text-gray-700">Participants</label>
              <input
                type="number"
                className={`w-full border-2 rounded-lg px-4 py-2 focus:ring-2 transition ${form.errors.participant ? 'border-red-400 focus:ring-red-300' : 'border-indigo-200 focus:ring-indigo-400'}`}
                value={form.participant}
                onChange={e => form.setParticipant(e.target.value)}
              />
              {form.errors.participant && <p className="text-sm text-red-500 mt-1">{form.errors.participant}</p>}
            </div>
            <div>
              <label className="block font-semibold mb-1 text-gray-700">Start Date</label>
              <input
                type="date"
                className={`w-full border-2 rounded-lg px-4 py-2 focus:ring-2 transition ${form.errors.startDate ? 'border-red-400 focus:ring-red-300' : 'border-indigo-200 focus:ring-indigo-400'}`}
                value={form.startDate}
                onChange={e => form.setStartDate(e.target.value)}
              />
              {form.errors.startDate && <p className="text-sm text-red-500 mt-1">{form.errors.startDate}</p>}
            </div>
            <div>
              <label className="block font-semibold mb-1 text-gray-700">End Date</label>
              <input
                type="date"
                className={`w-full border-2 rounded-lg px-4 py-2 focus:ring-2 transition ${form.errors.endDate ? 'border-red-400 focus:ring-red-300' : 'border-indigo-200 focus:ring-indigo-400'}`}
                value={form.endDate}
                onChange={e => form.setEndDate(e.target.value)}
              />
              {form.errors.endDate && <p className="text-sm text-red-500 mt-1">{form.errors.endDate}</p>}
            </div>
            <div className="md:col-span-2">
              <label className="block font-semibold mb-1 text-gray-700">Description</label>
              <textarea
                className={`w-full border-2 rounded-lg px-4 py-2 focus:ring-2 transition min-h-[70px] ${form.errors.description ? 'border-red-400 focus:ring-red-300' : 'border-indigo-200 focus:ring-indigo-400'}`}
                value={form.description}
                onChange={e => form.setDescription(e.target.value)}
              ></textarea>
              {form.errors.description && <p className="text-sm text-red-500 mt-1">{form.errors.description}</p>}
            </div>
          </div>
        </section>

        <section>
          <h2 className="text-2xl font-bold mb-4 text-indigo-700 border-b pb-2 border-indigo-100">Agreements</h2>
          <TermsInputGroup
            termsOfPolicy={form.termsOfPolicy} setTermsOfPolicy={form.setTermsOfPolicy}
            privacyPolicy={form.privacyPolicy} setPrivacyPolicy={form.setPrivacyPolicy}
            healthDataConsent={form.healthDataConsent} setHealthDataConsent={form.setHealthDataConsent}
            airDataConsent={form.airDataConsent} setAirDataConsent={form.setAirDataConsent}
            localDataTermsOfService={form.localDataTermsOfService} setLocalDataTermsOfService={form.setLocalDataTermsOfService}
            showHealth={form.showHealth} showAir={form.showAir}
            errors={form.errors}
          />
        </section>

        <section>
          <h2 className="text-2xl font-bold mb-4 text-indigo-700 border-b pb-2 border-indigo-100">Data Collection</h2>
          <DataCollectionSection
            personalInfo={form.personalInfo} setPersonalInfo={form.setPersonalInfo}
            healthData={form.healthData} setHealthData={form.setHealthData}
            airData={form.airData} setAirData={form.setAirData}
            showHealth={form.showHealth} showAir={form.showAir}
          />
        </section>

        {form.showAir && (
          <section>
            <h2 className="text-2xl font-bold mb-4 text-indigo-700 border-b pb-2 border-indigo-100">Air Metadata</h2>
            <AirMetaDataList
              airMetaDataItems={form.airMetaDataItems}
              updateMetaDataItem={form.updateMetaDataItem}
              addMetaData={form.addMetaData}
              removeMetaData={form.removeMetaData}
              errors={form.errors}
              showAddButtonOnly={form.airMetaDataItems.length === 0}
            />
          </section>
        )}

        <div className="text-right mt-8">
          <button className="bg-indigo-600 hover:bg-indigo-700 text-white font-bold px-8 py-3 rounded-xl shadow-lg text-lg transition" onClick={handleRegister}>
            Register
          </button>
        </div>
      </div>
    </div>
  );
}

export default ProjectCreatePage;
