% [im,path] = loadData(path)
% ---------------------------------------
%
% Generic function to load arbitrary image stacks in the workspace
%
% Inputs:
%  path        	Full path of the file, if not provided the function will call uigetfile
%
% Outputs:
%  im        	Loaded image
%  path 		Image full path
%
% ---------------------------------------
%
%   Copyright © 2018 Adrien Descloux - adrien.descloux@epfl.ch, 
%   École Polytechnique Fédérale de Lausanne, LBEN/LOB,
%   BM 5.134, Station 17, 1015 Lausanne, Switzerland.
%
%  	This program is free software: you can redistribute it and/or modify
%  	it under the terms of the GNU General Public License as published by
% 	the Free Software Foundation, either version 3 of the License, or
%  	(at your option) any later version.
%
%  	This program is distributed in the hope that it will be useful,
%  	but WITHOUT ANY WARRANTY; without even the implied warranty of
%  	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
%  	GNU General Public License for more details.
%
% 	You should have received a copy of the GNU General Public License
%  	along with this program.  If not, see <http://www.gnu.org/licenses/>.

function [im,path] = loadData(path)
if nargin < 1
    [fname,pname] = uigetfile('*.*');
    path = [pname,filesep,fname];
end
    

keepReading = 1; k = 1;
warning('off')
while keepReading 
    try
        im(:,:,k) = imread(path,k);
        k = k+1;
    catch
        keepReading = 0;
        disp('Finished reading... ')
        disp(['Stack size : ',num2str(size(im))])
    end
end
warning('on')