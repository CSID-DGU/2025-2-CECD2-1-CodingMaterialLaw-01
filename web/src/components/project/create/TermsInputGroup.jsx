function TermsInputGroup({
  termsOfPolicy, setTermsOfPolicy,
  privacyPolicy, setPrivacyPolicy,
  healthDataConsent, setHealthDataConsent,
  airDataConsent, setAirDataConsent,
  localDataTermsOfService, setLocalDataTermsOfService,
  showHealth, showAir,
  errors,
  readOnly = false
}) {
  const inputStyle = (errorKey) =>
    `w-full border-2 rounded-lg px-4 py-2 transition min-h-[70px] shadow-sm ${
      readOnly
        ? 'bg-gray-100 border-gray-200'
        : errors?.[errorKey]
        ? 'border-red-400 focus:ring-2 focus:ring-red-300'
        : 'border-indigo-200 focus:ring-2 focus:ring-indigo-400'
    }`;

  const errorText = (key) =>
    !readOnly && errors?.[key] && <p className="text-sm text-red-500 mt-1">{errors[key]}</p>;

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
      <div className="flex flex-col gap-1">
        <label className="font-semibold text-indigo-700 mb-1">Terms of Service</label>
        <textarea
          className={inputStyle('termsOfPolicy')}
          placeholder="Terms of Service"
          value={termsOfPolicy}
          onChange={(e) => setTermsOfPolicy(e.target.value)}
          readOnly={readOnly}
        />
        {errorText('termsOfPolicy')}
      </div>

      <div className="flex flex-col gap-1">
        <label className="font-semibold text-indigo-700 mb-1">Privacy Policy</label>
        <textarea
          className={inputStyle('privacyPolicy')}
          placeholder="Privacy Policy"
          value={privacyPolicy}
          onChange={(e) => setPrivacyPolicy(e.target.value)}
          readOnly={readOnly}
        />
        {errorText('privacyPolicy')}
      </div>

      {showHealth && (
        <div className="flex flex-col gap-1">
          <label className="font-semibold text-indigo-700 mb-1">Health Data Consent</label>
          <textarea
            className={inputStyle('healthDataConsent')}
            placeholder="Health Data Consent"
            value={healthDataConsent}
            onChange={(e) => setHealthDataConsent(e.target.value)}
            readOnly={readOnly}
          />
          {errorText('healthDataConsent')}
        </div>
      )}

      {showAir && (
        <div className="flex flex-col gap-1">
          <label className="font-semibold text-indigo-700 mb-1">Air Data Consent</label>
          <textarea
            className={inputStyle('airDataConsent')}
            placeholder="Air Data Consent"
            value={airDataConsent}
            onChange={(e) => setAirDataConsent(e.target.value)}
            readOnly={readOnly}
          />
          {errorText('airDataConsent')}
        </div>
      )}

      <div className="flex flex-col gap-1 md:col-span-2">
        <label className="font-semibold text-indigo-700 mb-1">Location Data Terms of Service</label>
        <textarea
          className={inputStyle('localDataTermsOfService')}
          placeholder="Location Data Terms of Service"
          value={localDataTermsOfService}
          onChange={(e) => setLocalDataTermsOfService(e.target.value)}
          readOnly={readOnly}
        />
        {errorText('localDataTermsOfService')}
      </div>
    </div>
  );
}

export default TermsInputGroup;
