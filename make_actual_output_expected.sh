echo "===== PLEASE BE ABSOLUTELY SURE ITS OK TO OVERWRITE THE EXPECTED OUTPUT WITH ACTUAL OUTPUT - IN ALL TESTS!! ====="
echo "to abort now, press Ctrl-C"
read i

mv -f tmp_aligned20_80_also_pardefs.xml           test/correct_output_aligned20_80_also_pardefs.xml 
mv -f tmp_testDicFormatE1Line-eo-en.xml           test/correct_output_DicFormatE1Line-eo-en.xml
mv -f tmp_testDicFormat.xml                       test/correct_output_DicFormat.xml
mv -f tmp_testprintXML_std1line.xml               test/correct_output_DicFormatE1Line.xml
mv -f tmp_testprintXML_stdaligned1line-eo-en.xml  test/correct_output_DicFormatE1LineAligned-eo-en.xml

cd regression_test_data/
cd crossdict/
mv -f actual_output/* expected_output/
cd ..
cd dic-reader/
mv -f actual_output.txt expected_output.txt 
cd ..
cd merge-morph/
mv actual_output.dix expected_output.dix 
cd ..
cd reverse-bil/
mv actual_output.dix expected_output.dix 
cd ..
cd sort/
mv actual_output.dix expected_output.dix 
mv -f actual_output/* expected_output/
cd ..
