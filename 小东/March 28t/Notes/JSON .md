---
title: JSON 
---
### JSON(JavaScript Object Notation)是一种数据交换格式。
#### 保存数据到JSON文件中
+ 创建一个JSONArray集合，调用它的put方法把数据放到集合中（先创建一个toJSONF方法把数据转换成JSONObject类型，再放到集合中），再调用outputStream类，把数据写到储存空间里面去。

        /**
         * 保存crime记录到JSON
         * @param crimes
         * @throws JSONException
         * @throws IOException
         */
        public void saveCrimes(ArrayList<Crime> crimes) throws JSONException,IOException{
            //Build an array in JSON
            JSONArray array=new JSONArray();
            for (Crime c:crimes){
                array.put(c.toJSON());
            }
            //write a file in disk
            Writer writer=null;
            try{
                OutputStream out=mContext.openFileOutput(mFilename,Context.MODE_PRIVATE);
                writer.write(array.toString());
            }finally {
                if (writer!=null){
                    writer.close();
                }
            }
        }
        
+ 接下来在存放数据的类中编写toJSON方法：

        private static final String JSON_ID="id";
            private static final String JSON_TITLE="title";
            private static final String JSON_SOLVED="solved";
            private static final String JSON_DATE="date";
            
            ...
            
            /**
             * 把crimes记录存在JSON里面去
             * @return
             * @throws JSONException
             */
            public JSONObject toJSON() throws JSONException{
                JSONObject json=new JSONObject();
                json.put(JSON_ID,id.toString());
                json.put(JSON_TITLE,title);
                json.put(JSON_SOLVED,solved);
                json.put(JSON_DATE,date.getTime());
                return  json;
            }
            
+ 再在其他有关的类中调用saveCrimes方法：

        public boolean saveCrimes() {
                try {
                    mSerializer.saveCrimes(mCrimes);
                    Log.d(TAG, "crimes save to file");
                    return true;
                } catch (Exception e) {
                    Log.e(TAG, "error saving crimes", e);
                    return false;
                }
            }

+最后在activity或者fragment调用saveCrimes方法的时候应该在onPause方法中进行。

        @Override
            public void onPause() {
                super.onPause();
                CrimeLab.get(getActivity()).saveCrimes();
            }

#### 从文件中读取JSON数据
+ 在相关的类中接受一个JSONObject对象的构造方法。
        
            /**
             * 从文件中读取crime数据
             * @param json
             * @throws JSONException
             */
            public Crime(JSONObject json) throws JSONException{
                id=UUID.fromString(json.getString(JSON_ID));
                if (json.has(JSON_TITLE)){
                    title=json.getString(JSON_TITLE);
                }
                solved=json.getBoolean(JSON_SOLVED);
                date=new Date(json.getLong(JSON_DATE));
            }

+ 添加一个从文件中加载数据记录的loadCrimes方法。

            /**
             * 加载crime记录的loadCrimes()方法
             * @return
             * @throws IOException
             * @throws JSONException
             */
            public ArrayList<Crime> loadCrimes() throws IOException,JSONException{
                ArrayList<Crime> crimes=new ArrayList<>();
                BufferedReader reader=null;
                try {
                    //open and read the file into a StringBuilder
                    InputStream in=mContext.openFileInput(mFilename);
                    reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder jsonString=new StringBuilder();
                    String line=null;
                    while ((line=reader.readLine())!=null){
                        //line breaks and omitted and irrelevant
                        jsonString.append(line);
                    }
                    //parse this JSON using JSONTokener
                    JSONArray array= (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
                    //build the array of crimes from JSONObjects
                    for (int i = 0; i < array.length(); i++) {
                        crimes.add(new Crime(array.getJSONObject(i)));
                    }
                }catch (FileNotFoundException e){
                    //Ignore this one,it happen when starting fresh
                }finally {
                    if (reader!=null){
                        reader.close();
                    }
                }
                return crimes;
            }
            
