function AirMetaDataList({
  airMetaDataItems,
  updateMetaDataItem,
  addMetaData,
  removeMetaData,
  errors = {},
  readOnly = false
}) {
  const getError = (id) => errors[`airMetaDataItems.${id}.dataName`];

  return (
    <>
      {airMetaDataItems.length === 0 && !readOnly ? (
        <div className="flex justify-start">
          <button type="button" className="text-xl border rounded px-3 py-2" onClick={addMetaData}>
            + Add Air Meta Data
          </button>
        </div>
      ) : (
        <>
          {airMetaDataItems.map((item, index) => {
            const error = getError(item.id);
            return (
              <div key={item.id} className="grid grid-cols-2 md:grid-cols-4 gap-4 items-start mb-2">
                <div className="flex flex-col">
                  <input
                    type="text"
                    className={`h-[44px] border rounded px-3 py-2 ${error ? 'border-red-400' : ''}`}
                    placeholder="Data Name"
                    value={item.dataName}
                    onChange={(e) => updateMetaDataItem(index, 'dataName', e.target.value)}
                    readOnly={readOnly}
                  />
                  {error && <p className="text-sm text-red-500 mt-1">{error}</p>}
                </div>
                <select
                  className="h-[44px] border rounded px-3 py-2"
                  value={item.dataType}
                  onChange={(e) => updateMetaDataItem(index, 'dataType', e.target.value)}
                  disabled={readOnly}
                >
                  <option value="INTEGER">INTEGER</option>
                  <option value="DOUBLE">DOUBLE</option>
                  <option value="STRING">STRING</option>
                  <option value="BOOLEAN">BOOLEAN</option>
                </select>
                {!readOnly && (
                  <>
                    <button type="button" className="text-xl h-[44px]" onClick={addMetaData}>+</button>
                    <button type="button" className="text-xl text-red-500 h-[44px]" onClick={() => removeMetaData(item.id)}>-</button>
                  </>
                )}
              </div>
            );
          })}
        </>
      )}
    </>
  );
}

export default AirMetaDataList;
